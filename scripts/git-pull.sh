#!/bin/sh

JAVA_CODE_PATH=$code_home/java
ANGULAR_CODE_PATH=$code_home/angular

cd $JAVA_CODE_PATH/SeerlogicsBom
git pull origin develop

cd $JAVA_CODE_PATH/EventGenie-Angular-Spring/eg-commons
git pull origin eventGenie-develop

cd $JAVA_CODE_PATH/EventGenie-Angular-Spring/eg-spring
git pull origin eventGenie-develop

cd $JAVA_CODE_PATH/SeerlogicsCloud
git pull origin develop

cd $JAVA_CODE_PATH/SeerlogicsBotCommons
git pull origin develop

cd $JAVA_CODE_PATH/SeerlogicsBotAdmin
git pull origin develop

cd $JAVA_CODE_PATH/SeerLogicsSharedBot
git pull origin develop

cd $ANGULAR_CODE_PATH/Angular-Library-With-NgPackagr
git pull origin develop

cd $ANGULAR_CODE_PATH/SeerlogicsBotAdminUI
git pull origin develop

cd $ANGULAR_CODE_PATH/SeerlogicsChatClientUI
git pull origin develop