package com.seerlogics.botadmin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Created by bkane on 1/10/19.
 * https://github.com/eugenp/tutorials/blob/5a0de6091bab2ca3a3a42e6d8851ad4a7c5a3f14/spring-boot/src/main/java/com/baeldung/properties/ConfigProperties.java
 * https://www.callicoder.com/spring-boot-configuration-properties-example/
 * https://www.baeldung.com/configuration-properties-in-spring-boot
 * https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-typesafe-configuration-properties
 */
@Component
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {
    private String artifactS3BucketName;
    private String awsCredentialProfileName;
    private String cleanBuildScript;
    private String launchBotScript;
    private String killBotScript;
    private String jwtSecretKey;
    private String jwtSignatureAlgo;
    private Long jwtTtl;
    private String botAppContext;

    private String runEnvironment;
    private String botReferencebotLocation;
    private String botArtifact;
    private String botActiveProfile;

    private String cloudProvider;
    private String chatAppDomain;

    // instance properties
    private String instanceReferenceImageId;
    private String instanceType;
    private String instanceKey;
    private String instanceRole;
    private String instanceSecurityProfileName;
    private String instanceSecurityGroups;
    private String instanceAvailabilityZones;
    private String instanceNameSuffix;

    // elb properties
    private String elbSecurityGroup;
    private String elbAvailabilityZones;
    private String elbHealthCheckUrl;
    private String elbInstancePort;
    private String elbNameSuffix;

    private boolean useH2Db = false;
    private String h2DbPath;
    private String h2BotDb;
    private String h2BotAdminDb;

    public String getChatAppDomain() {
        return chatAppDomain;
    }

    public void setChatAppDomain(String chatAppDomain) {
        this.chatAppDomain = chatAppDomain;
    }

    public String getElbNameSuffix() {
        return elbNameSuffix;
    }

    public void setElbNameSuffix(String elbNameSuffix) {
        this.elbNameSuffix = elbNameSuffix;
    }

    public String getCloudProvider() {
        return cloudProvider;
    }

    public void setCloudProvider(String cloudProvider) {
        this.cloudProvider = cloudProvider;
    }

    public String getInstanceNameSuffix() {
        return instanceNameSuffix;
    }

    public void setInstanceNameSuffix(String instanceNameSuffix) {
        this.instanceNameSuffix = instanceNameSuffix;
    }

    public String getElbAvailabilityZones() {
        return elbAvailabilityZones;
    }

    public void setElbAvailabilityZones(String elbAvailabilityZones) {
        this.elbAvailabilityZones = elbAvailabilityZones;
    }

    public String getElbHealthCheckUrl() {
        return elbHealthCheckUrl;
    }

    public void setElbHealthCheckUrl(String elbHealthCheckUrl) {
        this.elbHealthCheckUrl = elbHealthCheckUrl;
    }

    public String getElbInstancePort() {
        return elbInstancePort;
    }

    public void setElbInstancePort(String elbInstancePort) {
        this.elbInstancePort = elbInstancePort;
    }

    public String getElbSecurityGroup() {
        return elbSecurityGroup;
    }

    public void setElbSecurityGroup(String elbSecurityGroup) {
        this.elbSecurityGroup = elbSecurityGroup;
    }

    public String getInstanceAvailabilityZones() {
        return instanceAvailabilityZones;
    }

    public void setInstanceAvailabilityZones(String instanceAvailabilityZones) {
        this.instanceAvailabilityZones = instanceAvailabilityZones;
    }

    public String getInstanceSecurityProfileName() {
        return instanceSecurityProfileName;
    }

    public void setInstanceSecurityProfileName(String instanceSecurityProfileName) {
        this.instanceSecurityProfileName = instanceSecurityProfileName;
    }

    public String getH2DbPath() {
        return h2DbPath;
    }

    public void setH2DbPath(String h2DbPath) {
        this.h2DbPath = h2DbPath;
    }

    public String getH2BotDb() {
        return h2BotDb;
    }

    public void setH2BotDb(String h2BotDb) {
        this.h2BotDb = h2BotDb;
    }

    public String getH2BotAdminDb() {
        return h2BotAdminDb;
    }

    public void setH2BotAdminDb(String h2BotAdminDb) {
        this.h2BotAdminDb = h2BotAdminDb;
    }

    public boolean isUseH2Db() {
        return useH2Db;
    }

    public void setUseH2Db(boolean useH2Db) {
        this.useH2Db = useH2Db;
    }

    public String getBotActiveProfile() {
        return botActiveProfile;
    }

    public void setBotActiveProfile(String botActiveProfile) {
        this.botActiveProfile = botActiveProfile;
    }

    public String getBotArtifact() {
        return botArtifact;
    }

    public void setBotArtifact(String botArtifact) {
        this.botArtifact = botArtifact;
    }

    public String getBotReferencebotLocation() {
        return botReferencebotLocation;
    }

    public void setBotReferencebotLocation(String botReferencebotLocation) {
        this.botReferencebotLocation = botReferencebotLocation;
    }

    public String getKillBotScript() {
        return killBotScript;
    }

    public void setKillBotScript(String killBotScript) {
        this.killBotScript = killBotScript;
    }

    public String getLaunchBotScript() {
        return launchBotScript;
    }

    public void setLaunchBotScript(String launchBotScript) {
        this.launchBotScript = launchBotScript;
    }

    public String getRunEnvironment() {
        return runEnvironment;
    }

    public void setRunEnvironment(String runEnvironment) {
        this.runEnvironment = runEnvironment;
    }

    public String getInstanceRole() {
        return instanceRole;
    }

    public void setInstanceRole(String instanceRole) {
        this.instanceRole = instanceRole;
    }

    public String getInstanceKey() {
        return instanceKey;
    }

    public void setInstanceKey(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public String getInstanceSecurityGroups() {
        return instanceSecurityGroups;
    }

    public void setInstanceSecurityGroups(String instanceSecurityGroups) {
        this.instanceSecurityGroups = instanceSecurityGroups;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getInstanceReferenceImageId() {
        return instanceReferenceImageId;
    }

    public void setInstanceReferenceImageId(String instanceReferenceImageId) {
        this.instanceReferenceImageId = instanceReferenceImageId;
    }

    public String getBotAppContext() {
        return botAppContext;
    }

    public void setBotAppContext(String botAppContext) {
        this.botAppContext = botAppContext;
    }

    public String getJwtSignatureAlgo() {
        return jwtSignatureAlgo;
    }

    public void setJwtSignatureAlgo(String jwtSignatureAlgo) {
        this.jwtSignatureAlgo = jwtSignatureAlgo;
    }

    public Long getJwtTtl() {
        return jwtTtl;
    }

    public void setJwtTtl(Long jwtTtl) {
        this.jwtTtl = jwtTtl;
    }

    public String getJwtSecretKey() {
        return jwtSecretKey;
    }

    public void setJwtSecretKey(String jwtSecretKey) {
        this.jwtSecretKey = jwtSecretKey;
    }

    public String getCleanBuildScript() {
        return cleanBuildScript;
    }

    public void setCleanBuildScript(String cleanBuildScript) {
        this.cleanBuildScript = cleanBuildScript;
    }

    public String getAwsCredentialProfileName() {
        return awsCredentialProfileName;
    }

    public void setAwsCredentialProfileName(String awsCredentialProfileName) {
        this.awsCredentialProfileName = awsCredentialProfileName;
    }

    public String getArtifactS3BucketName() {
        return artifactS3BucketName;
    }

    public void setArtifactS3BucketName(String artifactS3BucketName) {
        this.artifactS3BucketName = artifactS3BucketName;
    }
}
