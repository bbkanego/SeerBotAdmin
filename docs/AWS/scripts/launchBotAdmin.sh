#!/bin/bash

# This is what the below script will do:
# First you copy the JAR and H2 DB to S3 bucket.
# Next launch T2 instance
# Copy the JAR and H2 from S3 to instance.
# Start the Boot JAR on the instance.

s3commonKeyPrefix='bkane/BotAdminApp/'

botAdminArtifact='seerlogics-bot-admin-1.0.0-SNAPSHOT.jar'
botAdminBlob='~/svn/code/java/SeerlogicsBotAdmin/target/'$botAdminArtifact
s3botAdminDataKey=$s3commonKeyPrefix$botAdminArtifact

botDBArtifact='botDB.mv.db'
botDBBlob='~/svn/code/java/SeerlogicsBotAdmin/h2/'$botDBArtifact
s3botDBDataKey=$s3commonKeyPrefix$botDBArtifact

chatBotDbArtifact='chatBotServerDB.mv.db'
chatBotDbBlob='~/svn/code/java/SeerlogicsBotAdmin/h2/'$chatBotDbArtifact
s3chatBotDBDataKey=$s3commonKeyPrefix$chatBotDbArtifact

bucket='biz-bot-artifact'

echo 'build BotAdmin artifact now'
cd ~/svn/code/java/SeerlogicsBotAdmin
mvn clean install -P aws-ec2

echo 'Build done ------------------'

cd ~/svn/code/java/SeerlogicsBotAdmin/docs/AWS/scripts

# cd to ~/Users/bkane/svn/code/java/SeerlogicsBotAdmin/target
echo 'Copying deployable artifacts to S3: ' $botAdminBlob $botDBBlob $chatBotDbBlob
aws s3api put-object --profile bizBotAdmin --body $botAdminBlob --bucket $bucket --key $s3botAdminDataKey
aws s3api put-object --profile bizBotAdmin --body $botDBBlob --bucket $bucket --key $s3botDBDataKey
aws s3api put-object --profile bizBotAdmin --body $chatBotDbBlob --bucket $bucket --key $s3chatBotDBDataKey

echo 'Launching instances now'
#aws ec2 run-instances --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=BizBotAdmin}]' --region us-east-2 --profile bizBotAdmin --image-id ami-0cd3dfa4e37921605 --key-name bizBotAdminLogin --security-groups bizBotSecurityGroup --instance-type t2.micro --placement AvailabilityZone=us-east-2c --block-device-mappings DeviceName=/dev/sdh,Ebs={VolumeSize=100} --count 1
aws ec2 run-instances --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=BizBotAdmin}]' --user-data file://onLaunchScript.sh --region us-east-2 --iam-instance-profile Name="S3_biz_bot_artifact_Readonly" --image-id ami-0cd3dfa4e37921605 --key-name bizBotAdminLogin --security-groups bizBotSecurityGroup --instance-type t2.micro --placement AvailabilityZone=us-east-2c --count 1

