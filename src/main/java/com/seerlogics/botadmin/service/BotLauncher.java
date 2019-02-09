package com.seerlogics.botadmin.service;

import com.seerlogics.botadmin.config.AppProperties;
import com.seerlogics.botadmin.dto.LaunchModel;
import com.seerlogics.botadmin.event.InstanceLaunchedEvent;
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
import com.lingoace.util.RunScript;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

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

    @Async("launchBotTaskExecutor")
    @Transactional
    public void launchBotAsync(LaunchModel launchModel) {
        if ("local".equals(appProperties.getRunEnvironment())) {
            this.launchBotAsyncLocal(launchModel);
        } else {
            this.launchBotAsyncCloud(launchModel);
        }
    }

    private void launchBotAsyncLocal(LaunchModel launchModel) {
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
        InstanceLaunchedEvent instanceLaunchedEvent = new InstanceLaunchedEvent(this, "Instance Published");
        applicationEventPublisher.publishEvent(instanceLaunchedEvent);
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
        InstanceLaunchedEvent instanceLaunchedEvent = new InstanceLaunchedEvent(this, "Instance Published");
        applicationEventPublisher.publishEvent(instanceLaunchedEvent);
    }
}
