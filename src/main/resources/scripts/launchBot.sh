#!/bin/bash
#SOURCE_CODE="/home/bkane/svn/code/java/SpringMVC/chatNLPApp"
#SOURCE_MAIN="$SOURCE_CODE/src/main"
#TARGET="/home/bkane/svn/code/java/BotAdmin/src/main/resources/apps/chatbot/src/main"
#echo "Copying files from " $SOURCE_MAIN/java " to " $TARGET/java
#cp -a $SOURCE_MAIN/java/. $TARGET/java/
#echo "Copying files from " $SOURCE_MAIN/resources " to " $TARGET/resources
#cp -a $SOURCE_MAIN/resources/. $TARGET/resources/
echo "======================================Launching the BOT now======================================"
cd /home/bkane/svn/code/java/BotAdmin/src/main/resources/apps/chatbot
echo $M2_HOME
echo $MAVEN_HOME
# The nohup utility makes the command passed as an argument run in the background even after you log out.
# The & symbol, switches the program to run in the background. --server.port=8020
echo "Starting the bot on the port = " $1
echo "With context path = " $2
nohup java -jar target/chatbot-0.0.1-SNAPSHOT.jar $1 $2 > ~/nohup.out 2> ~/nohup.err < /dev/null &
#rm -rf target
echo "======================================Launching the BOT complete======================================"