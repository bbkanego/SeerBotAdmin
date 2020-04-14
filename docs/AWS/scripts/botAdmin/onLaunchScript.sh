#!/bin/bash -ex

instanceArtifactLocation='/home/ec2-user/seerBotAdmin'
botAdminArtifact='seerlogics-bot-admin-1.0.0-SNAPSHOT.war'
serverXML='server.xml'
s3commonKeyPrefix='bkane/BotAdminApp/seerBotAdmin/'

s3botAdminDataKey=$s3commonKeyPrefix$botAdminArtifact

s3serverXMLKey=$s3commonKeyPrefix$serverXML

bucket='biz-bot-artifact'

echo 'Make code directory:' $instanceArtifactLocation
su - ec2-user -c 'mkdir -p /home/ec2-user/seerBotAdmin'

echo 'log the actions,install java 8 and remove java 1.7'
set -x
#  print commands and their arguments with "set -x", redirect all output with bash's exec builtin, the logger syslog tool, or both:
#  The "/var/log/cloud-init-output.log" will also log that on the instance. But will not have all the logs.
exec > >(tee /var/log/seer-user-data.log|logger -t user-data -s 2>/dev/console) 2>&1
echo BEGIN
date '+%Y-%m-%d %H:%M:%S'
echo END
sudo yum install -y java-1.8.0-openjdk.x86_64
sudo /usr/sbin/alternatives --set java /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/java
#sudo /usr/sbin/alternatives --set java /usr/lib/jvm/jre-1.8.0/bin/java
#sudo /usr/sbin/alternatives --set javac /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/bin/javac
#sudo yum remove java-1.7

su - ec2-user -c 'cd /home/ec2-user/seerBotAdmin'

echo 'Make DIR for logs'
su - ec2-user -c 'mkdir /home/ec2-user/seerBotAdmin/logs'

echo 'Copy the BotAdmin artifact from' $bucket 'with key' $s3botAdminDataKey
su - ec2-user -c 'aws s3api get-object --bucket biz-bot-artifact --key bkane/BotAdminApp/seerBotAdmin/seerlogics-bot-admin-1.0.0-SNAPSHOT.war /home/ec2-user/seerBotAdmin/seerlogics-bot-admin-1.0.0-SNAPSHOT.war'

#Now install tomcat 9
su - ec2-user -c  'mkdir -p /home/ec2-user/installs/tomcat'
su - ec2-user -c  'cd /home/ec2-user/installs/tomcat'
echo 'Copy Tomcat 9.0.33 from S3 bucket'
su - ec2-user -c 'aws s3api get-object --bucket biz-bot-artifact --key bkane/BotAdminApp/seerBotAdmin/apache-tomcat-9.0.33.tar.gz apache-tomcat-9.0.33.tar.gz'

su - ec2-user -c  'tar xzvf apache-tomcat-9.0.33.tar.gz -C /home/ec2-user/installs/tomcat --strip-components=1'

echo 'Copy the Server.xml artifact from' $bucket 'with key' $s3serverXMLKey
su - ec2-user -c 'aws s3api get-object --bucket biz-bot-artifact --key bkane/BotAdminApp/seerBotAdmin/server.xml /home/ec2-user/installs/tomcat/conf/server.xml'

echo 'Make scripts active in tomcat'
su - ec2-user -c 'chmod +x /home/ec2-user/installs/tomcat/bin/*.sh'

echo 'Starting the BotAdminApp'
su - ec2-user -c '/home/ec2-user/installs/tomcat/bin/startup.sh'

echo 'register the instance with elb'
instanceId="`wget -q -O - http://169.254.169.254/latest/meta-data/instance-id`"
echo "---->>> Instance id is = " $instanceId
aws elb register-instances-with-load-balancer --region us-east-1 --load-balancer-name BizBotAdminELB --instances $instanceId

echo 'Deleting deployable artifact' $s3botAdminDataKey
#su - ec2-user -c 'aws s3api delete-object --bucket biz-bot-artifact --key bkane/BotAdminApp/seerBotAdmin/seerlogics-bot-admin-1.0.0-SNAPSHOT.war'
#su - ec2-user -c 'aws s3api delete-object --bucket biz-bot-artifact --key bkane/BotAdminApp/seerBotAdmin/apache-tomcat-9.0.33.tar.gz'
