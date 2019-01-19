#!/bin/bash
echo "======================================MAVEN CLEAN START======================================"
cd /home/bkane/svn/code/java/BotAdmin/src/main/resources/apps/chatbot
mvn clean install -Dspring.profiles.active=aws-ec2
echo "======================================MAVEN CLEAN END======================================"