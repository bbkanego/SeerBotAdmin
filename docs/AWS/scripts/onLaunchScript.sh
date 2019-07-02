#!/bin/bash

instanceArtifactLocation='/home/ec2-user/seerBotAdmin'
botAdminArtifact='seerlogics-bot-admin-1.0.0-SNAPSHOT.war'
botDB='botDB.mv.db'
chatBotDB='chatBotServerDB.mv.db'
serverXML='server.xml'
s3commonKeyPrefix='bkane/BotAdminApp/'

s3botAdminDataKey=$s3commonKeyPrefix$botAdminArtifact

s3botDBDataKey=$s3commonKeyPrefix$botDB

s3chatBotDBDataKey=$s3commonKeyPrefix$chatBotDB

s3serverXMLKey=$s3commonKeyPrefix$serverXML

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
su - ec2-user -c 'aws s3api get-object --bucket biz-bot-artifact --key bkane/BotAdminApp/seerlogics-bot-admin-1.0.0-SNAPSHOT.war /home/ec2-user/seerBotAdmin/seerlogics-bot-admin-1.0.0-SNAPSHOT.war'
echo 'Copy the BotDB artifact from' $bucket 'with key' $s3botDBDataKey
su - ec2-user -c 'aws s3api get-object --bucket biz-bot-artifact --key bkane/BotAdminApp/botDB.mv.db /home/ec2-user/seerBotAdmin/botDB.mv.db'
echo 'Copy the ChatBotDB artifact from' $bucket 'with key' $s3chatBotDBDataKey
su - ec2-user -c 'aws s3api get-object --bucket biz-bot-artifact --key bkane/BotAdminApp/chatBotServerDB.mv.db /home/ec2-user/seerBotAdmin/chatBotServerDB.mv.db'

# https://www.digitalocean.com/community/tutorials/install-tomcat-9-ubuntu-1804
#echo 'Create a non-privilaged user for tomcat'
#sudo groupadd tomcat

# Next, create a new tomcat user. We'll make this user a member of the tomcat group, with a
# home directory of /opt/tomcat (where we will install Tomcat), and with a
# shell of /bin/false (so nobody can log into the account):
#sudo useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat
su - ec2-user -c  'mkdir -p /home/ec2-user/installs/tomcat'
su - ec2-user -c  'cd /home/ec2-user/installs/tomcat'
echo 'Download Tomcat 9.0.21'
su - ec2-user -c 'curl -O http://mirror.cc.columbia.edu/pub/software/apache/tomcat/tomcat-9/v9.0.21/bin/apache-tomcat-9.0.21.tar.gz'

su - ec2-user -c  'tar xzvf apache-tomcat-9.0.21.tar.gz -C /home/ec2-user/installs/tomcat --strip-components=1'
su - ec2-user -c  'rm /home/ec2-user/installs/tomcat/apache-tomcat-9.0.21.tar.gz'

echo 'Copy the Server.xml artifact from' $bucket 'with key' $s3serverXMLKey
su - ec2-user -c 'aws s3api get-object --bucket biz-bot-artifact --key bkane/BotAdminApp/server.xml /home/ec2-user/installs/tomcat/conf/server.xml'

echo 'Make scripts active in tomcat'
su - ec2-user -c 'chmod +x /home/ec2-user/installs/tomcat/bin/*.sh'

echo 'Starting the BotAdminApp'
su - ec2-user -c '/home/ec2-user/installs/tomcat/bin/startup.sh'

echo 'Deleting deployable artifact' $s3botAdminDataKey
su - ec2-user -c 'aws s3api delete-object --bucket biz-bot-artifact --key bkane/BotAdminApp/seerlogics-bot-admin-1.0.0-SNAPSHOT.war'
echo 'Deleting deployable artifact' $s3botDBDataKey
su - ec2-user -c 'aws s3api delete-object --bucket biz-bot-artifact --key bkane/BotAdminApp/botDB.mv.db botDB.mv.db'
echo 'Deleting deployable artifact' $s3chatBotDBDataKey
su - ec2-user -c 'aws s3api delete-object --bucket biz-bot-artifact --key bkane/BotAdminApp/chatBotServerDB.mv.db chatBotServerDB.mv.db'
su - ec2-user -c 'aws s3api delete-object --bucket biz-bot-artifact --key bkane/BotAdminApp/server.xml server.xml'