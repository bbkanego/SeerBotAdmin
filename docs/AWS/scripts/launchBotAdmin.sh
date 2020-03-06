#!/bin/bash

# This is what the below script will do:
# First you copy the JAR and H2 DB to S3 bucket.
# Next launch T2 instance
# Copy the JAR and H2 from S3 to instance.
# Start the Boot JAR on the instance.

s3commonKeyPrefix='bkane/BotAdminApp/'

botAdminArtifact='seerlogics-bot-admin-1.0.0-SNAPSHOT.war'
botAdminBlob=$code_home'/java/SeerlogicsBotAdmin/target/'$botAdminArtifact
s3botAdminDataKey=$s3commonKeyPrefix$botAdminArtifact

serverXMLArtifact='server.xml'
serverXMLBlob=$code_home'/java/SeerlogicsBotAdmin/tomcat/AWS/'$serverXMLArtifact
s3serverXMLDataKey=$s3commonKeyPrefix$serverXMLArtifact

botDBArtifact='botDB.mv.db'
botDBBlob=$code_home'/java/SeerlogicsBotAdmin/h2/'$botDBArtifact
s3botDBDataKey=$s3commonKeyPrefix$botDBArtifact

chatBotDbArtifact='chatBotServerDB.mv.db'
chatBotDbBlob=$code_home'/java/SeerlogicsBotAdmin/h2/'$chatBotDbArtifact
s3chatBotDBDataKey=$s3commonKeyPrefix$chatBotDbArtifact

bucket='biz-bot-artifact'

$code_home/java/SeerlogicsBotAdmin/docs/AWS/scripts/terminateEC2Instances.sh

echo 'build BotAdmin artifact now'
cd $code_home/java/SeerlogicsBotAdmin
mvn clean install -P aws-ec2-war

echo 'Build done ------------------'

cd $code_home/java/SeerlogicsBotAdmin/docs/AWS/scripts

# cd to ~$code_home/java/SeerlogicsBotAdmin/target
echo 'Copying deployable artifacts to S3: ' $botAdminBlob $botDBBlob $chatBotDbBlob $serverXMLBlob
aws s3api put-object --profile bizBotAdmin --body $botAdminBlob --bucket $bucket --key $s3botAdminDataKey
aws s3api put-object --profile bizBotAdmin --body $botDBBlob --bucket $bucket --key $s3botDBDataKey
aws s3api put-object --profile bizBotAdmin --body $chatBotDbBlob --bucket $bucket --key $s3chatBotDBDataKey
aws s3api put-object --profile bizBotAdmin --body $serverXMLBlob --bucket $bucket --key $s3serverXMLDataKey

echo 'Launching instances now'
# AWS Amazon linux image id: ami-0998bf58313ab53da
#aws ec2 run-instances --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=BizBotAdmin}]' --region us-east-2 --profile bizBotAdmin --image-id ami-0cd3dfa4e37921605 --key-name bizBotAdminLogin --security-groups bizBotSecurityGroup --instance-type t2.micro --placement AvailabilityZone=us-east-2c --block-device-mappings DeviceName=/dev/sdh,Ebs={VolumeSize=100} --count 1
aws ec2 run-instances --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=BizBotAdmin}]' --user-data file://onLaunchScript.sh --region us-east-2 --iam-instance-profile Name="S3_biz_bot_artifact_Readonly" --image-id ami-0cd3dfa4e37921605 --key-name bizBotAdminLogin --security-groups bizBotSecurityGroup --instance-type t2.micro --placement AvailabilityZone=us-east-2c --count 2

echo 'create ELB and configure it'
aws elb create-load-balancer --load-balancer-name BizBotAdminELB --listeners "Protocol=HTTP,LoadBalancerPort=80,InstanceProtocol=HTTP,InstancePort=8099" --subnets subnet-5adb8617 --security-groups sg-066e46dd3f09492bd
aws elb configure-health-check --load-balancer-name BizBotAdminELB --health-check Target=HTTP:8099/botadmin/actuator/info,Interval=30,UnhealthyThreshold=2,HealthyThreshold=2,Timeout=3