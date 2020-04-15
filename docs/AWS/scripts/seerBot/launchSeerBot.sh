#!/bin/bash

# This is what the below script will do:
# First you copy the JAR and H2 DB to S3 bucket.
# Next launch T2 instance
# Copy the JAR and H2 from S3 to instance.
# Start the Boot JAR on the instance.

s3commonKeyPrefix='bkane/BotAdminApp/seerBot/'

# Here we are getting the BotAdmin Artifact ready. Here we provide path the artifact that will copied to S3 bucket
sharedBotArtifact='seerlogics-shared-bot-1.0.0-SNAPSHOT.war'
# Define the blob info
sharedBotBlob=$code_home'/java/SeerLogicsSharedBot/target/'$sharedBotArtifact
# Define the S3 Key.
s3SharedBotDataKey=$s3commonKeyPrefix$sharedBotArtifact

# Here we are getting the XML that we need to copy to tomcat.
serverXMLArtifact='server.xml'
# We define the artifact here
serverXMLBlob=$code_home'/java/SeerlogicsBotAdmin/tomcat/AWS/seerBot/'$serverXMLArtifact
# We then define the key under which the XML will be copied over to S3 bucket
s3serverXMLDataKey=$s3commonKeyPrefix$serverXMLArtifact

# Here we are getting the XML that we need to copy to tomcat.
tomcat9Artifact='apache-tomcat-9.0.33.tar.gz'
# We define the artifact here
tomcat9ArtifactBlob=$code_home'/java/SeerlogicsBotAdmin/tomcat/'$tomcat9Artifact
# We then define the key under which the XML will be copied over to S3 bucket
s3serverTomcat9DataKey=$s3commonKeyPrefix$tomcat9Artifact

bucket='biz-bot-artifact'

$code_home/java/SeerlogicsBotAdmin/docs/AWS/scripts/seerBot/terminateSeerBotInstances.sh

echo 'build Shared Bot artifact now'
cd $code_home/java/SeerLogicsSharedBot
mvn clean install -DskipTests -P aws-ec2-war

echo 'Build done ------------------'

cd $code_home/java/SeerlogicsBotAdmin/docs/AWS/scripts/seerBot

# cd to ~$code_home/java/SeerlogicsBotAdmin/target
echo 'Copying deployable artifacts to S3: ' $sharedBotBlob $tomcat9ArtifactBlob $serverXMLBlob
aws s3api put-object --profile bizBotAdmin --body $sharedBotBlob --bucket $bucket --key $s3SharedBotDataKey
aws s3api put-object --profile bizBotAdmin --body $tomcat9ArtifactBlob --bucket $bucket --key $s3serverTomcat9DataKey
aws s3api put-object --profile bizBotAdmin --body $serverXMLBlob --bucket $bucket --key $s3serverXMLDataKey

echo 'Launching instances now: https://docs.aws.amazon.com/cli/latest/reference/ec2/run-instances.html'
# AWS Amazon linux image id: ami-0cd3dfa4e37921605
aws ec2 run-instances --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=SeerBotInstance}]' --user-data file://onLaunchBotScript.sh --placement AvailabilityZone=us-east-1c --iam-instance-profile Name="S3_biz_bot_artifact_Readonly" --image-id ami-0915e09cc7ceee3ab --key-name SeerGabAdminKeyPair --security-group-ids sg-06772c33fac14313f --instance-type t2.micro --count 2

#echo 'create ELB and configure it'
#aws elb create-load-balancer --load-balancer-name SeerBotELB --listeners "Protocol=HTTP,LoadBalancerPort=80,InstanceProtocol=HTTP,InstancePort=8099" --security-groups sg-06772c33fac14313f --subnets subnet-223bec0c
#aws elb configure-health-check --load-balancer-name SeerBotELB --health-check Target=HTTP:8099/chatbot/actuator/info,Interval=30,UnhealthyThreshold=2,HealthyThreshold=2,Timeout=3
