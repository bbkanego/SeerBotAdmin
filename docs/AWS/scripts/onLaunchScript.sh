#!/bin/bash

instanceArtifactLocation='/home/ec2-user/seerBotAdmin'
botAdminArtifact='seerlogics-bot-admin-1.0.0-SNAPSHOT.jar'
botDB='botDB.mv.db'
chatBotDB='chatBotServerDB.mv.db'
s3commonKeyPrefix='bkane/BotAdminApp/'

s3botAdminDataKey=$s3commonKeyPrefix$botAdminArtifact

s3botDBDataKey=$s3commonKeyPrefix$botDB

s3chatBotDBDataKey=$s3commonKeyPrefix$chatBotDB

bucket='biz-bot-artifact'

echo 'Make code directory:' $instanceArtifactLocation
su - ec2-user -c 'mkdir -p /home/ec2-user/seerBotAdmin'

echo 'log the actions,install java 8 and remove java 1.7'
#set -x\n
#sudo exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1
sudo yum install -y java-1.8.0-openjdk.x86_64
sudo /usr/sbin/alternatives --set java /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/java
sudo /usr/sbin/alternatives --set javac /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/javac
sudo yum remove java-1.7

su - ec2-user -c 'cd /home/ec2-user/seerBotAdmin'

echo 'Make DIR for logs'
su - ec2-user -c 'mkdir /home/ec2-user/seerBotAdmin/logs'

echo 'Copy the BotAdmin artifact from' $bucket 'with key' $s3botAdminDataKey
su - ec2-user -c 'aws s3api get-object --bucket biz-bot-artifact --key bkane/BotAdminApp/seerlogics-bot-admin-1.0.0-SNAPSHOT.jar /home/ec2-user/seerBotAdmin/seerlogics-bot-admin-1.0.0-SNAPSHOT.jar'
echo 'Copy the BotDB artifact from' $bucket 'with key' $s3botDBDataKey
su - ec2-user -c 'aws s3api get-object --bucket biz-bot-artifact --key bkane/BotAdminApp/botDB.mv.db /home/ec2-user/seerBotAdmin/botDB.mv.db'
echo 'Copy the ChatBotDB artifact from' $bucket 'with key' $s3chatBotDBDataKey
su - ec2-user -c 'aws s3api get-object --bucket biz-bot-artifact --key bkane/BotAdminApp/chatBotServerDB.mv.db /home/ec2-user/seerBotAdmin/chatBotServerDB.mv.db'

echo 'Starting the BotAdminApp'
su - ec2-user -c 'java -jar /home/ec2-user/seerBotAdmin/seerlogics-bot-admin-1.0.0-SNAPSHOT.jar'

echo 'Deleting deployable artifact' $s3botAdminDataKey
su - ec2-user -c 'aws s3api delete-object --bucket biz-bot-artifact --key bkane/BotAdminApp/seerlogics-bot-admin-1.0.0-SNAPSHOT.jar'
echo 'Deleting deployable artifact' $s3botDBDataKey
su - ec2-user -c 'aws s3api delete-object --bucket biz-bot-artifact --key bkane/BotAdminApp/botDB.mv.db botDB.mv.db'
echo 'Deleting deployable artifact' $s3chatBotDBDataKey
su - ec2-user -c 'aws s3api delete-object --bucket biz-bot-artifact --key bkane/BotAdminApp/chatBotServerDB.mv.db chatBotServerDB.mv.db'