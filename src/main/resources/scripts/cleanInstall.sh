#!/bin/bash
echo "======================================MAVEN CLEAN START======================================"
cd ~/svn/code/java/SeerlogicsBotAdmin/src/main/resources/apps/chatbot
#mvn clean install -Dspring.profiles.active=aws-ec2
mvn clean install -Dspring.profiles.active=local
echo "======================================MAVEN CLEAN END======================================"