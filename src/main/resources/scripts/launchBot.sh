#!/bin/bash
echo "======================================Launching the BOT now======================================"
echo $M2_HOME
echo $MAVEN_HOME
echo "Arg 1 =" $1
echo "Arg 2 =" $2
echo "Arg 3 =" $3
echo "Arg 4 =" $4
echo "Arg 5 =" $5
echo "Arg 6 =" $6
echo "Arg 7 =" $7
echo "Arg 8 =" $8
echo "Arg 9 =" $9
echo "Arg 10 =" $10
#cd ~/svn/code/java/SeerlogicsBotAdmin/src/main/resources/apps/chatbot
cd $1
# The nohup utility makes the command passed as an argument run in the background even after you log out.
# The & symbol, switches the program to run in the background. --server.port=8020
cp target/$2 ..
cd ..
#example: java -jar -Dspring.profiles.active=local target/chatbot-0.0.1-SNAPSHOT.jar
nohup java -jar $2 $3 $4 $5 $6 $7 $8 $9 $10 > ~/nohup.out 2> ~/nohup.err < /dev/null &

until [ "`curl --silent --show-error --connect-timeout 1 -I http://localhost:8099 | grep '404'`" != "" ];
do
  echo --- sleeping for 10 seconds
  sleep 10
done

echo Tomcat is ready!
echo delete the temp dir
rm -rf $1
# echo the PID
echo $!
echo "======================================Launching the BOT complete======================================"