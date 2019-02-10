#!/bin/bash
echo "======================================Launching the BOT now======================================"
cd ~/svn/code/java/SeerlogicsBotAdmin/src/main/resources/apps/chatbot
echo $M2_HOME
echo $MAVEN_HOME
# The nohup utility makes the command passed as an argument run in the background even after you log out.
# The & symbol, switches the program to run in the background. --server.port=8020
nohup java -jar -Dspring.profiles.active=local target/chatbot-0.0.1-SNAPSHOT.jar > ~/nohup.out 2> ~/nohup.err < /dev/null &
#rm -rf target
echo "======================================Launching the BOT complete======================================"