package com.seerlogics.botadmin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
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
    private String botArtifactName;
    private String botArtifactType;
    private String botArtifactLocation;
    private String jwtSecretKey;
    private String jwtSignatureAlgo;
    private Long jwtTtl;
    private String botAppContext;
    private String referenceImageId;
    private String instanceType;
    private String instanceKey;
    private String instanceRole;
    private String securityGroup;
    private String runEnvironment;

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

    public String getSecurityGroup() {
        return securityGroup;
    }

    public void setSecurityGroup(String securityGroup) {
        this.securityGroup = securityGroup;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getReferenceImageId() {
        return referenceImageId;
    }

    public void setReferenceImageId(String referenceImageId) {
        this.referenceImageId = referenceImageId;
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

    public String getBotArtifactType() {
        return botArtifactType;
    }

    public void setBotArtifactType(String botArtifactType) {
        this.botArtifactType = botArtifactType;
    }

    public String getBotArtifactName() {
        return botArtifactName;
    }

    public void setBotArtifactName(String botArtifactName) {
        this.botArtifactName = botArtifactName;
    }

    public String getBotArtifactLocation() {
        return botArtifactLocation;
    }

    public void setBotArtifactLocation(String botArtifactLocation) {
        this.botArtifactLocation = botArtifactLocation;
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
