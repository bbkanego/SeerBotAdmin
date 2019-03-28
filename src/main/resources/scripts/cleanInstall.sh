#!/bin/bash
echo "======================================MAVEN CLEAN START======================================"
echo "Arg 1" $1
echo "Arg 2" $2
#cd ~/svn/code/java/SeerlogicsBotAdmin/src/main/resources/apps/chatbot
cd $1
#example: mvn clean install -DskipTests -Dspring.profiles.active=aws-ec2
mvn clean install $2
echo "======================================MAVEN CLEAN END======================================"