#!/bin/bash
echo "======================================MAVEN CLEAN START======================================"
#cd ~/svn/code/java/SeerlogicsBotAdmin/src/main/resources/apps/chatbot
cd $1
#mvn clean install -Dspring.profiles.active=aws-ec2
mvn clean install $2
echo "======================================MAVEN CLEAN END======================================"