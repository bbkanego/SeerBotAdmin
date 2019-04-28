package com.seerlogics.botadmin.service;

import com.lingoace.util.RunScript;
import com.seerlogics.botadmin.config.AppProperties;
import com.seerlogics.botadmin.event.InstanceLaunchedEvent;
import com.seerlogics.botadmin.event.InstanceRestartedEvent;
import com.seerlogics.botadmin.event.InstanceStoppedEvent;
import com.seerlogics.botadmin.exception.LaunchBotException;
import com.seerlogics.botadmin.exception.StopBotException;
import com.seerlogics.botadmin.factory.ManageDataStoreFactory;
import com.seerlogics.botadmin.factory.ManageInstanceFactory;
import com.seerlogics.botadmin.factory.ManageLoadBalancerFactory;
import com.seerlogics.cloud.BucketConfiguration;
import com.seerlogics.cloud.ManageDataStore;
import com.seerlogics.cloud.aws.ec2.AwsInstanceConfiguration;
import com.seerlogics.cloud.aws.elb.AwsLoadBalancerConfiguration;
import com.seerlogics.cloud.aws.model.LaunchLoadBalancerResult;
import com.seerlogics.commons.dto.LaunchModel;
import com.seerlogics.commons.model.Bot;
import com.seerlogics.commons.model.Configuration;
import com.seerlogics.commons.model.Status;
import com.seerlogics.commons.model.TrainedModel;
import com.seerlogics.commons.repository.BotRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bkane on 12/30/18.
 */
@Component
@Transactional
public class BotLauncher {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BotLauncher.class);

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private ManageDataStoreFactory manageDataStoreFactory;

    @Autowired
    private ManageInstanceFactory manageInstanceFactory;

    @Autowired
    private ManageLoadBalancerFactory manageLoadBalancerFactory;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private StatusService statusService;

    @Autowired
    private TrainedModelService trainedModelService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Async("stopBotTaskExecutor")
    @Transactional
    public void stopBotAsync(Long id) {
        if ("local".equals(appProperties.getRunEnvironment())) {
            this.stopBotAsyncLocal(id);
        } else {
            this.stopBotAsyncCloud(id);
        }
        InstanceStoppedEvent instanceStoppedEvent = new InstanceStoppedEvent(this, "Bot Stopped");
        applicationEventPublisher.publishEvent(instanceStoppedEvent);
    }

    @Async("restartBotTaskExecutor")
    @Transactional
    public void restartBotAsync(Long id) {
        if ("local".equals(appProperties.getRunEnvironment())) {
            this.restartBotAsyncLocal(id);
        } else {
            this.restartBotAsyncCloud(id);
        }
        InstanceRestartedEvent instanceRestartedEvent = new InstanceRestartedEvent(this, "Bot Restarted");
        applicationEventPublisher.publishEvent(instanceRestartedEvent);
    }

    @Async("launchBotTaskExecutor")
    @Transactional
    public void launchBotAsync(LaunchModel launchModel) {
        if ("local".equals(appProperties.getRunEnvironment())) {
            this.launchBotAsyncLocal(launchModel);
        } else {
            this.launchBotAsyncCloud(launchModel);
        }

        InstanceLaunchedEvent instanceLaunchedEvent = new InstanceLaunchedEvent(this, "Bot Launched");
        applicationEventPublisher.publishEvent(instanceLaunchedEvent);
    }

    private void stopBotAsyncLocal(Long id) {
        try {
            // get the bot and model to use
            Bot bot = this.botRepository.getOne(id);
            Configuration configuration = (Configuration) bot.getConfigurations().toArray()[0];
            // kill the bot
            File killBotScript = ResourceUtils.getFile("classpath:" + appProperties.getKillBotScript());
            RunScript.runCommand("chmod +x " + killBotScript.getAbsolutePath());
            RunScript.runCommand(killBotScript.getAbsolutePath());

            bot.setStatus(statusService.findByCode(Status.STATUS_CODES.DRAFT.name()));
            bot.getConfigurations().clear();
            this.botRepository.save(bot);
            File workingFolder = new File(configuration.getWorkingFolder());
            FileUtils.deleteDirectory(workingFolder);
        } catch (Exception e) {
            throw new LaunchBotException(e);
        }
    }

    private void restartBotAsyncLocal(Long id) {
        try {
            // kill the bot
            File killBotScript = ResourceUtils.getFile("classpath:" + appProperties.getKillBotScript());
            RunScript.runCommand("chmod +x " + killBotScript.getAbsolutePath());
            RunScript.runCommand(killBotScript.getAbsolutePath());

            // next launch the bot
            File launchBotScript = ResourceUtils.getFile("classpath:" + appProperties.getLaunchBotScript());
            RunScript.runCommand("chmod +x " + launchBotScript.getAbsolutePath());
            RunScript.runCommand(launchBotScript.getAbsolutePath());
        } catch (Exception e) {
            throw new LaunchBotException(e);
        }
    }

    private void restartBotAsyncCloud(Long id) {

    }

    private void stopBotAsyncCloud(Long id) {
        // get the bot and model to use
        Bot bot = this.botRepository.getOne(id);
        Configuration configuration = (Configuration) bot.getConfigurations().toArray()[0];
        configuration.getLoadBalancerName();

        try {
            String[] instanceIds = StringUtils.split(configuration.getInstanceIds(), ",");
            List<String> instanceIdsList = Arrays.asList(instanceIds);
            manageInstanceFactory.createInstanceManager(appProperties).terminateInstances(instanceIdsList);

            AwsLoadBalancerConfiguration awsLoadBalancerConfiguration =
                    new AwsLoadBalancerConfiguration(configuration.getLoadBalancerName(), null,
                            null, null, null, 0);
            manageLoadBalancerFactory.createLoadBalancerManager(appProperties)
                    .deleteLoadBalancer(awsLoadBalancerConfiguration);
        } catch (Exception e) {
            throw new StopBotException("Problems stopping bot with id = " + bot.getId());
        }
    }

    /**
     * Locate the reference bot which you will copy for the customer and run/build in a custom location.
     * example. We will copy the bot from a reference location: ~/svn/code/java/SeerLogicsReferenceBot to an
     * account specific location and build it there.<br/>
     * <b>Local Demo:</b> If its local demo then we will run the bot from that location.<br/>
     * <b>PROD deploy:</b> If its PROD then we will actually the build bot from there to S3 bucket and then delete the account specific
     * DIR.
     *
     * @param bot          TrainedModel
     * @param trainedModel Bot
     * @return File
     */
    private File copyBotToCustomLocation(TrainedModel trainedModel, Bot bot) {
        String referenceBotPath = System.getProperty("user.home") + appProperties.getBotReferencebotLocation();
        String accountSpecificFolderName = "account_" + trainedModel.getOwner().getId() + File.separator +
                "bot_" + bot.getId();
        String accountSpecificBotBuildDirParent = System.getProperty("user.home") + File.separator + "seerBots"
                + File.separator + accountSpecificFolderName;
        String accountSpecificBotBuildDirPath = accountSpecificBotBuildDirParent + File.separator + "chatbot";
        File accountSpecificBotBuildDir = new File(accountSpecificBotBuildDirPath);
        try {
            // remove existing content if any
            if (accountSpecificBotBuildDir.exists()) {
                FileUtils.cleanDirectory(accountSpecificBotBuildDir);
            } else {
                // if not then create the dir
                if (accountSpecificBotBuildDir.mkdirs()) {
                    LOGGER.debug(">>>> Directory created: " + accountSpecificBotBuildDir.getAbsolutePath());
                }
            }
            // copy reference bot to the temp dir
            File srcDir = new File(referenceBotPath);
            File destDir = new File(accountSpecificBotBuildDir.getAbsolutePath());
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LaunchBotException(e);
        }
        return accountSpecificBotBuildDir;
    }

    /**
     * https://stackoverflow.com/questions/21059085/how-can-i-create-a-file-in-the-current-users-home-directory-using-java
     *
     * @param launchModel
     */
    private void launchBotAsyncLocal(LaunchModel launchModel) {
        TrainedModel trainedModel = trainedModelService.getSingle(launchModel.getTrainedModelId());

        // get the bot and model to use
        Bot bot = this.botRepository.getOne(launchModel.getBot().getId());

        File accountSpecificBotBuildDir = copyBotToCustomLocation(trainedModel, bot);

        LOGGER.debug("Launching the bot now >>>>>>>>>>>>>>>>>>>>>>");

        try {
            // Since this is local, only one bot can run at a time. So kill any other bots.
            File killBotScript = ResourceUtils.getFile("classpath:" + appProperties.getKillBotScript());
            RunScript.runCommand("chmod +x " + killBotScript.getAbsolutePath());
            RunScript.runCommand(killBotScript.getAbsolutePath());

            /**
             * First go to the custom build directory and do a clean build. This will create a target dir with
             * chatbot jar
             */
            File cleanBuildScript = ResourceUtils.getFile("classpath:" + appProperties.getCleanBuildScript());
            RunScript.runCommand("chmod +x " + cleanBuildScript.getAbsolutePath());
            // a clean build with all the args required
            String botArtifactName = appProperties.getBotArtifact();
            RunScript.runCommandWithArgs(cleanBuildScript.getAbsolutePath(),
                    accountSpecificBotBuildDir.getAbsolutePath(),
                    "-Djar.finalName=" + botArtifactName,
                    "-DskipTests",
                    "-Dspring.profiles.active=local");

            int botPort = 8099;
            if (launchModel.getPort() != null) {
                botPort = launchModel.getPort();
            }

            /**
             * Next launch the bot by going to the custom build directory.
             * Args provided to script:
             * 1. Path to absolute path to the custom bot.
             * 2. Profile of the chatbot
             * 3. Category Type of the bot.
             */
            String args1 = accountSpecificBotBuildDir.getAbsolutePath();
            String args2 = "-Dspring.profiles.active=local";
            String args4 = "--seerchat.bottype=" + launchModel.getBot().getCategory().getCode();
            String args5 = "--seerchat.botOwnerId=" + launchModel.getBot().getOwner().getId();
            String args6 = "--seerchat.botId=" + launchModel.getBot().getId();
            String args7 = "--seerchat.trainedModelId=" + launchModel.getTrainedModelId();
            String args8 = "--seerchat.botPort=" + botPort;
            File launchBotScript = ResourceUtils.getFile("classpath:" + appProperties.getLaunchBotScript());
            RunScript.runCommand("chmod +x " + launchBotScript.getAbsolutePath());
            RunScript.runCommandWithArgs(launchBotScript.getAbsolutePath(), args1, args2, botArtifactName,
                    args4, args5, args6, args7, args8);

            bot.setStatus(statusService.findByCode(Status.STATUS_CODES.LAUNCHED.name()));
            if (bot.getConfigurations().size() == 0) {
                Configuration configuration = new Configuration();
                configuration.setTrainedModel(trainedModel);
                configuration.setEnvironment(Status.STATUS_CODES.LAUNCHED.name());
                configuration.setUrl("http://localhost:8099/chatbot/api/chats");
                configuration.setPort(botPort);
                configuration.setWorkingFolder(accountSpecificBotBuildDir.getAbsolutePath());
                configuration.setImageIds("localhost");
                configuration.setInstanceIds("localhost");
                configuration.setPublicDns("localhost");
                bot.getConfigurations().add(configuration);
            }

            this.botRepository.save(bot);
            LOGGER.debug("Launching the bot done. Upload succeeded >>>>>>>>>>>>>>>>>> ");
            InstanceLaunchedEvent instanceLaunchedEvent = new InstanceLaunchedEvent(this, "Instance Published");
            applicationEventPublisher.publishEvent(instanceLaunchedEvent);

        } catch (Exception e) {
            e.printStackTrace();
            throw new LaunchBotException(e);
        }
    }

    private void launchBotAsyncCloud(LaunchModel launchModel) {
        try {
            String contextPath = appProperties.getBotAppContext();
            TrainedModel trainedModel = trainedModelService.getSingle(launchModel.getTrainedModelId());

            // get the bot and model to use
            Bot bot = this.botRepository.getOne(launchModel.getBot().getId());

            // copy the reference bot to a custom location.
            File accountSpecificBotBuildDir = copyBotToCustomLocation(trainedModel, bot);
            String profile = "aws-ec2";

            LOGGER.debug("Launching the bot now >>>>>>>>>>>>>>>>>>>>>>");
            /**
             * First go to the custom build directory and do a clean build. This will create a target dir with
             * chatbot jar
             */
            LOGGER.debug("Build the artifact -------");
            File cleanBuildScript = ResourceUtils.getFile("classpath:" + appProperties.getCleanBuildScript());
            RunScript.runCommand("chmod +x " + cleanBuildScript.getAbsolutePath());
            String chatbotArtifact = appProperties.getBotArtifact();
            // run the clean build with 2 args.
            // a clean build with all the args required
            String botArtifactName = appProperties.getBotArtifact();
            RunScript.runCommandWithArgs(cleanBuildScript.getAbsolutePath(),
                    accountSpecificBotBuildDir.getAbsolutePath(),
                    "-Djar.finalName=" + botArtifactName,
                    "-DskipTests",
                    "-P " + profile);
            LOGGER.debug("Build the artifact DONE -------");

            /**
             * Once the above build succeeds we will copy the bot jar to S3 bucket for the account.
             */
            LOGGER.debug("Copy the artifact -------");
            /**
             * Lets first work on how to define the bucket key for each artifact.
             */
            String s3TopLevelBucketKey = launchModel.getOwnerUserName() + "/" + bot.getName() + "_" + bot.getId();
            // this specifies the account specific location
            String chatBotArtifactKey = s3TopLevelBucketKey + "/" + chatbotArtifact;
            // we need to upload the DB also which is needed for the chatbot to work.
            String botAdminDbArtifactKey = s3TopLevelBucketKey + "/" + appProperties.getH2BotAdminDb();
            String botDbArtifactKey = s3TopLevelBucketKey + "/" + appProperties.getH2BotDb();

            /**
             * Next we define location where the aws CLI will download the artifacts on each launched instance
             */
            String instanceArtifactLocation = "~/bot";
            String instanceChatBotArtifactKey = instanceArtifactLocation + "/" + chatbotArtifact;
            // we need to upload the DB also which is needed for the chatbot to work.
            String instanceBotAdminDbArtifactKey = instanceArtifactLocation + "/" + appProperties.getH2BotAdminDb();
            String instanceBotDbArtifactKey = instanceArtifactLocation + "/" + appProperties.getH2BotDb();

            BucketConfiguration bucketConfiguration = new BucketConfiguration();
            // bucket to copy the artifact to.
            bucketConfiguration.setBucketName(appProperties.getArtifactS3BucketName());

            bucketConfiguration.setFileObjKeyName(chatBotArtifactKey);
            // file that will copied from local to S3
            bucketConfiguration.setFileName(accountSpecificBotBuildDir.getAbsolutePath() + "/target/" + chatbotArtifact);
            // S3 auth credentials for the current user.
            bucketConfiguration.setCredentialProfileName(appProperties.getAwsCredentialProfileName());
            ManageDataStore manageDataStore = manageDataStoreFactory.createDataStoreManager(appProperties);
            manageDataStore.uploadToBucket(bucketConfiguration);
            LOGGER.debug("Copy the artifact DONE -------");

            if (appProperties.isUseH2Db()) {
                LOGGER.debug("Copy the H2DB -------");
                BucketConfiguration bucketConfigurationAdminDb = new BucketConfiguration();
                // bucket to copy the artifact to.
                bucketConfigurationAdminDb.setBucketName(appProperties.getArtifactS3BucketName());
                // this specifies the account specific location
                bucketConfigurationAdminDb.setFileObjKeyName(botAdminDbArtifactKey);
                // file that will copied from local to S3
                File botAdminDbFile = new File(System.getProperty("user.home") + appProperties.getH2DbPath() + appProperties.getH2BotAdminDb());
                bucketConfigurationAdminDb.setFileName(botAdminDbFile.getAbsolutePath());
                // S3 auth credentials for the current user.
                bucketConfigurationAdminDb.setCredentialProfileName(appProperties.getAwsCredentialProfileName());
                manageDataStore.uploadToBucket(bucketConfigurationAdminDb);

                BucketConfiguration bucketConfigurationBotDb = new BucketConfiguration();
                // bucket to copy the artifact to.
                bucketConfigurationBotDb.setBucketName(appProperties.getArtifactS3BucketName());
                // this specifies the account specific location
                bucketConfigurationBotDb.setFileObjKeyName(botDbArtifactKey);
                // file that will copied from local to S3
                File botDbFile = new File(System.getProperty("user.home") + appProperties.getH2DbPath() + appProperties.getH2BotDb());
                bucketConfigurationBotDb.setFileName(botDbFile.getAbsolutePath());
                // S3 auth credentials for the current user.
                bucketConfigurationBotDb.setCredentialProfileName(appProperties.getAwsCredentialProfileName());
                manageDataStore.uploadToBucket(bucketConfigurationBotDb);
                LOGGER.debug("Copy the H2DB DONE -------");
            }

            /**
             * https://swiftotter.com/technical/amazon-aws-jenkins-2-60-1-java-8-update
             * This defines any commands that we need to run when the EC2 instance is launched.
             * The below will remove JDK 1.7 and install JDK 1.8
             * Next it will copy the bot artifact to the instance and start it
             */

            int botPort = 8099;
            if (launchModel.getPort() != null) {
                botPort = launchModel.getPort();
            }
            //String args1 = " -Dspring.profiles.active=" + profile;
            String args2 = " --seerchat.bottype=" + launchModel.getBot().getCategory().getCode();
            String args3 = " --seerchat.botOwnerId=" + launchModel.getBot().getOwner().getId();
            String args4 = " --seerchat.botId=" + launchModel.getBot().getId();
            String args5 = " --seerchat.trainedModelId=" + launchModel.getTrainedModelId();
            String args6 = " --seerchat.botPort=" + botPort;

            String javaCmd = "su - ec2-user -c 'java -jar " + instanceChatBotArtifactKey + args2 + args3 +
                    args4 + args5 + args6 + "'\n";

            String copyBotArtifact = "su - ec2-user -c 'aws s3api get-object --bucket " + appProperties.getArtifactS3BucketName()
                    + " --key " + chatBotArtifactKey + " " + instanceChatBotArtifactKey + "'\n";

            /**
             * The aws cli command will look like this. No profile needs to be defined:
             * aws s3api get-object --bucket biz-bot-artifact --key bkane/EventMgmt-Bot_196/botDB.mv.db ~/bot/botDB.mv.db
             */
            if (appProperties.isUseH2Db()) {
                String copyBotAdminDb = "su - ec2-user -c 'aws s3api get-object --bucket " + appProperties.getArtifactS3BucketName()
                        + " --key " + botAdminDbArtifactKey + " " + instanceBotAdminDbArtifactKey + "'\n";

                String copyBotDb = "su - ec2-user -c 'aws s3api get-object --bucket " + appProperties.getArtifactS3BucketName()
                        + " --key " + botDbArtifactKey + " " + instanceBotDbArtifactKey + "'\n";

                copyBotArtifact += copyBotAdminDb + copyBotDb;
            }

            LOGGER.debug("Launch the instance -------");
            String scriptToRunOnInstanceLaunch = "#!/bin/bash\n" +
                    "set -x\n" + // set debug mode
                    /**
                     * direct the command execution to a log file where we can check how the commands faired.
                     * https://jee-appy.blogspot.com/2017/02/user-data-aws.html
                     */
                    "exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1\n" +
                    "sudo yum install -y java-1.8.0-openjdk.x86_64\n" +
                    "sudo /usr/sbin/alternatives --set java /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/java\n" +
                    "sudo /usr/sbin/alternatives --set javac /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/javac\n" +
                    "sudo yum remove java-1.7\n" +
                    // run below commands as ec2-user
                    "su - ec2-user -c 'mkdir " + instanceArtifactLocation + "'\n"
                    + copyBotArtifact
                    + javaCmd;

            // now launch the bots if not already present. We are launching 3 bot instances.
            String[] instanceAvailabilityZones = StringUtils.split(appProperties.getInstanceAvailabilityZones(), ",");
            String[] instanceSecurityGroups = StringUtils.split(appProperties.getInstanceSecurityGroups(), ",");
            AwsInstanceConfiguration instanceConfiguration = new AwsInstanceConfiguration(appProperties.getInstanceReferenceImageId(),
                    AwsInstanceConfiguration.AwsInstanceType.valueOf(appProperties.getInstanceType()),
                    Arrays.asList(instanceAvailabilityZones),
                    appProperties.getAwsCredentialProfileName(), 1, 1, appProperties.getInstanceKey(),
                    Arrays.asList(instanceSecurityGroups), scriptToRunOnInstanceLaunch,
                    appProperties.getInstanceRole(), appProperties.getInstanceSecurityProfileName(),
                    launchModel.getOwnerUserName() + appProperties.getInstanceNameSuffix());
            manageInstanceFactory.createInstanceManager(appProperties).launchInstance(instanceConfiguration);
            LOGGER.debug("Launch the instance DONE -------");

            LOGGER.debug("Launch the ELB -------");
            String[] elbSecurityGroups = StringUtils.split(appProperties.getElbSecurityGroup(), ",");
            String[] elbAvailabilityZones = StringUtils.split(appProperties.getElbAvailabilityZones(), ",");
            // next launch the ELB and register the instances just launched with the ELB.
            AwsLoadBalancerConfiguration awsLoadBalancerConfiguration =
                    new AwsLoadBalancerConfiguration(launchModel.getOwnerUserName() + appProperties.getElbNameSuffix(),
                            Arrays.asList(elbSecurityGroups), Arrays.asList(elbAvailabilityZones),
                            appProperties.getAwsCredentialProfileName(), appProperties.getElbHealthCheckUrl(),
                            Integer.parseInt(appProperties.getElbInstancePort()));
            LaunchLoadBalancerResult launchLoadBalancerResult =
                    manageLoadBalancerFactory.createLoadBalancerManager(appProperties).launchLoadBalancer(awsLoadBalancerConfiguration);
            LOGGER.debug("Launch the ELB DONE -------");

            LOGGER.debug("Saving to DB -------");
            bot.setStatus(statusService.findByCode(Status.STATUS_CODES.LAUNCHED.name()));
            if (bot.getConfigurations().size() == 0) {
                Configuration configuration = new Configuration();
                configuration.setTrainedModel(trainedModel);
                configuration.setEnvironment(Status.STATUS_CODES.LAUNCHED.name());
                configuration.setUrl("http://" + launchLoadBalancerResult.getElbDNS() + appProperties.getElbHealthCheckUrl());
                configuration.setPort(botPort);
                configuration.setImageIds(StringUtils.join(launchLoadBalancerResult.getImageIds(), ","));
                configuration.setInstanceIds(StringUtils.join(launchLoadBalancerResult.getInstanceIds(), ","));
                configuration.setPublicDns(launchLoadBalancerResult.getElbDNS());
                bot.getConfigurations().add(configuration);
            }

            this.botRepository.save(bot);
            LOGGER.debug("Saving to DB DONE -------");
            LOGGER.debug("Launching the bot done. Upload succeeded >>>>>>>>>>>>>>>>>> ");
        } catch (Exception e) {
            e.printStackTrace();
            throw new LaunchBotException(e);
        }
    }
}
