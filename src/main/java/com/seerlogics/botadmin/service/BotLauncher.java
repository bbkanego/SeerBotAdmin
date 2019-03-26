package com.seerlogics.botadmin.service;

import com.lingoace.util.RunScript;
import com.seerlogics.botadmin.config.AppProperties;
import com.seerlogics.botadmin.dto.LaunchModel;
import com.seerlogics.botadmin.event.InstanceLaunchedEvent;
import com.seerlogics.botadmin.event.InstanceRestartedEvent;
import com.seerlogics.botadmin.event.InstanceStoppedEvent;
import com.seerlogics.botadmin.exception.LaunchBotException;
import com.seerlogics.botadmin.model.Bot;
import com.seerlogics.botadmin.model.Configuration;
import com.seerlogics.botadmin.model.Status;
import com.seerlogics.botadmin.model.TrainedModel;
import com.seerlogics.botadmin.repository.BotRepository;
import com.seerlogics.cloud.BucketConfiguration;
import com.seerlogics.cloud.ManageDataStore;
import com.seerlogics.cloud.ManageInstance;
import com.seerlogics.cloud.ManageLoadBalancer;
import com.seerlogics.cloud.aws.ec2.AwsInstanceConfiguration;
import com.seerlogics.cloud.aws.elb.AwsLoadBalancerConfiguration;
import com.seerlogics.cloud.aws.model.LaunchLoadBalancerResult;
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
import java.io.IOException;
import java.util.UUID;

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
    private ManageDataStore manageDataStore;

    @Autowired
    private ManageInstance manageInstance;

    @Autowired
    private ManageLoadBalancer manageLoadBalancer;

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
        InstanceStoppedEvent instanceStoppedEvent = new InstanceStoppedEvent(this, "Instance Published");
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
            Configuration configuration = (Configuration)bot.getConfigurations().toArray()[0];
            // kill the bot
            File killBotScript = ResourceUtils.getFile("classpath:" + appProperties.getKillBotScript());
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
            RunScript.runCommand(killBotScript.getAbsolutePath());

            // next launch the bot
            File launchBotScript = ResourceUtils.getFile("classpath:" + appProperties.getLaunchBotScript());
            RunScript.runCommand(launchBotScript.getAbsolutePath());
        } catch (Exception e) {
            throw new LaunchBotException(e);
        }
    }

    private void restartBotAsyncCloud(Long id) {

    }

    private void stopBotAsyncCloud(Long id) {

    }

    /**
     * https://stackoverflow.com/questions/21059085/how-can-i-create-a-file-in-the-current-users-home-directory-using-java
     * @param launchModel
     */
    private void launchBotAsyncLocal(LaunchModel launchModel) {
        TrainedModel trainedModel = trainedModelService.getSingle(launchModel.getTrainedModelId());

        String folderName = UUID.randomUUID().toString();
        /**
         * Locate the reference bot which you will copy for the customer and run in a custom location.
         * reference location: /Users/bkane/seerBots/referenceBot/chatbot
         */
        String referenceBotPath =
                System.getProperty("user.home") + File.separator + "seerBots" + File.separator
                                                + "referenceBot" + File.separator + "chatbot";
        String botBuildDirParent = System.getProperty("user.home") + File.separator + "seerBots"
                                    + File.separator + folderName;
        String botBuildDirPath = botBuildDirParent + File.separator + "chatbot";
        File botBuildDir = new File(botBuildDirPath);
        if (!botBuildDir.exists()) {
            if (botBuildDir.mkdirs()) {
                LOGGER.debug(">>>> Directory created: " + botBuildDir.getAbsolutePath());
                // copy reference bot to the temp dir
                File srcDir = new File(referenceBotPath);
                File destDir = new File(botBuildDir.getAbsolutePath());
                try {
                    FileUtils.copyDirectory(srcDir, destDir);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new LaunchBotException(e);
                }
            }
        }

        // get the bot and model to use
        Bot bot = this.botRepository.getOne(launchModel.getBot().getId());

        LOGGER.debug("Launching the bot now >>>>>>>>>>>>>>>>>>>>>>");

        try {
            String modelCopyPath = botBuildDirPath + "/src/main/resources/nlp/models/custom/";
            String destFileName = "en-cat-eventgenie-intents-dynamic.bin";
            File destinationFile = new File(modelCopyPath + destFileName);
            // delete the existing file first.
            destinationFile.delete();
            FileUtils.writeByteArrayToFile(destinationFile, trainedModel.getFile());

            // Since this is local, only one bot can run at a time. So kill any other bots.
            File killBotScript = ResourceUtils.getFile("classpath:" + appProperties.getKillBotScript());
            RunScript.runCommand(killBotScript.getAbsolutePath());

            /**
             * First go to the custom build directory and do a clean build. This will create a target dir with
             * chatbot jar
             */
            File cleanBuildScript = ResourceUtils.getFile("classpath:" + appProperties.getCleanBuildScript());
            RunScript.runCommand("chmod +x " + cleanBuildScript.getAbsolutePath());
            // run the clean build with 2 args.
            RunScript.runCommandWithArgs(cleanBuildScript.getAbsolutePath(),
                            botBuildDir.getAbsolutePath(), " -DskipTests -Dspring.profiles.active=local");

            /**
             * Next launch the bot by going to the custom build directory.
             * Args provided to script:
             * 1. Path to absolute path to the custom bot.
             * 2. Profile of the chatbot
             * 3. Category Type of the bot.
             */
            String args1 = botBuildDir.getAbsolutePath();
            String args2 = "-Dspring.profiles.active=local";
            String args3 = " --seerchat.bottype=" + launchModel.getBot().getCategory().getCode() +
                    " --seerchat.botOwnerId=" + launchModel.getBot().getOwner().getId();
            File launchBotScript = ResourceUtils.getFile("classpath:" + appProperties.getLaunchBotScript());
            RunScript.runCommandWithArgs(launchBotScript.getAbsolutePath(), args1, args2, args3);

            bot.setStatus(statusService.findByCode(Status.STATUS_CODES.LAUNCHED.name()));
            if (bot.getConfigurations().size() == 0) {
                Configuration configuration = new Configuration();
                configuration.setTrainedModel(trainedModel);
                configuration.setEnvironment(Status.STATUS_CODES.LAUNCHED.name());
                configuration.setUrl("http://localhost:8099/chatbot/api/chats");
                configuration.setPort(8099);
                configuration.setWorkingFolder(botBuildDirParent);
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

    private void launchBotAsyncLocalOld(LaunchModel launchModel) {
        String contextPath = appProperties.getBotAppContext();
        // get the bot and model to use
        Bot bot = this.botRepository.getOne(launchModel.getBot().getId());

        LOGGER.debug("Launching the bot now >>>>>>>>>>>>>>>>>>>>>>");
        try {

            // Since this is local, only one bot can run at a time. So kill any other bots.
            File killBotScript = ResourceUtils.getFile("classpath:" + appProperties.getKillBotScript());
            RunScript.runCommand(killBotScript.getAbsolutePath());

            // first clean build the bot
            File cleanBuildScript = ResourceUtils.getFile("classpath:" + appProperties.getCleanBuildScript());
            RunScript.runCommand("chmod +x " + cleanBuildScript.getAbsolutePath());
            RunScript.runCommand(cleanBuildScript.getAbsolutePath());

            // next launch the bot
            File launchBotScript = ResourceUtils.getFile("classpath:" + appProperties.getLaunchBotScript());
            RunScript.runCommand(launchBotScript.getAbsolutePath());

            bot.setStatus(statusService.findByCode(Status.STATUS_CODES.LAUNCHED.name()));
            if (bot.getConfigurations().size() == 0) {
                TrainedModel trainedModel = this.trainedModelService.getSingle(launchModel.getTrainedModelId());
                Configuration configuration = new Configuration();
                configuration.setTrainedModel(trainedModel);
                configuration.setEnvironment(Status.STATUS_CODES.LAUNCHED.name());
                configuration.setUrl("http://localhost:8099/chatbot/api/chats");
                configuration.setPort(8099);
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
        String contextPath = appProperties.getBotAppContext();
        // get the bot and model to use
        Bot bot = this.botRepository.getOne(launchModel.getBot().getId());

        LOGGER.debug("Launching the bot now >>>>>>>>>>>>>>>>>>>>>>");
        RunScript.runCommand("chmod +x " + appProperties.getCleanBuildScript());
        RunScript.runCommand(appProperties.getCleanBuildScript());

        String s3BucketKey = launchModel.getOwnerUserName() + appProperties.getBotArtifactName();
        BucketConfiguration bucketConfiguration = new BucketConfiguration();
        bucketConfiguration.setBucketName(appProperties.getArtifactS3BucketName());
        bucketConfiguration.setFileObjKeyName(s3BucketKey);
        bucketConfiguration.setFileName(appProperties.getBotArtifactLocation() + "/"
                + appProperties.getBotArtifactName() + "." + appProperties.getBotArtifactType());
        bucketConfiguration.setCredentialProfileName(appProperties.getAwsCredentialProfileName());
        manageDataStore.uploadToBucket(bucketConfiguration);

        // https://swiftotter.com/technical/amazon-aws-jenkins-2-60-1-java-8-update
        String script = "#!/bin/bash\n" +
                "sudo yum install -y java-1.8.0-openjdk.x86_64\n" +
                "sudo /usr/sbin/alternatives --set java /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/java\n" +
                "sudo /usr/sbin/alternatives --set javac /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/javac\n" +
                "sudo yum remove java-1.7\n" +
                "aws s3api get-object --bucket biz-bot-artifact --key " + s3BucketKey + " chatbot-0.0.1.jar\n" +
                "java -jar -Dspring.profiles.active=aws-ec2 chatbot-0.0.1.jar\n";

        // now launch the bots if not already present.
        AwsInstanceConfiguration instanceConfiguration = new AwsInstanceConfiguration(appProperties.getReferenceImageId(),
                AwsInstanceConfiguration.AwsInstanceType.valueOf(appProperties.getInstanceType()),
                AwsInstanceConfiguration.DEFAULT_AZ,
                appProperties.getAwsCredentialProfileName(), 1, 3, appProperties.getInstanceKey(),
                appProperties.getSecurityGroup(), script,
                appProperties.getInstanceRole(), launchModel.getOwnerUserName());
        manageInstance.launchInstance(instanceConfiguration);

        // next launch the ELB and register the instances just launched with the ELB.
        AwsLoadBalancerConfiguration awsLoadBalancerConfiguration = new AwsLoadBalancerConfiguration(launchModel.getOwnerUserName(),
                appProperties.getSecurityGroup(), AwsInstanceConfiguration.DEFAULT_AZ,
                appProperties.getAwsCredentialProfileName());
        LaunchLoadBalancerResult launchLoadBalancerResult = manageLoadBalancer.launchLoadBalancer(awsLoadBalancerConfiguration);

        bot.setStatus(statusService.findByCode(Status.STATUS_CODES.LAUNCHED.name()));

        if (bot.getConfigurations().size() == 0) {
            TrainedModel trainedModel = this.trainedModelService.getSingle(launchModel.getTrainedModelId());
            Configuration configuration = new Configuration();
            configuration.setTrainedModel(trainedModel);
            configuration.setEnvironment(Status.STATUS_CODES.LAUNCHED.name());
            configuration.setUrl("http://" + launchLoadBalancerResult.getElbDNS() + contextPath + "/api/chats");
            configuration.setPort(5000);
            configuration.setImageIds(StringUtils.join(launchLoadBalancerResult.getImageIds(), ","));
            configuration.setInstanceIds(StringUtils.join(launchLoadBalancerResult.getInstanceIds(), ","));
            configuration.setPublicDns(launchLoadBalancerResult.getElbDNS());
            bot.getConfigurations().add(configuration);
        }

        this.botRepository.save(bot);
        LOGGER.debug("Launching the bot done. Upload succeeded >>>>>>>>>>>>>>>>>> ");
    }
}
