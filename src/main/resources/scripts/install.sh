#!/bin/bash
echo "======================================MAVEN INSTALL START======================================"
cd /home/bkane/svn/code/java/BotAdmin/src/main/resources/apps/chatbot
mvn install -Dspring.profiles.active=aws-ec2
echo "======================================MAVEN INSTALL END======================================"