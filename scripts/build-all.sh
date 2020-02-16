#!/bin/sh

JAVA_CODE_PATH=~/Bhushan/code/java
ANGULAR_CODE_PATH=~/Bhushan/code/angular

./git-pull.sh

mvn -f $JAVA_CODE_PATH/SeerlogicsBom/pom.xml  clean install

mvn -f $JAVA_CODE_PATH/EventGenie-Angular-Spring/eg-commons/pom.xml  clean install

mvn -f $JAVA_CODE_PATH/EventGenie-Angular-Spring/eg-spring/pom.xml  clean install

mvn -f $JAVA_CODE_PATH/SeerlogicsCloud/pom.xml  clean install

mvn -f $JAVA_CODE_PATH/SeerlogicsBotCommons/pom.xml  clean install

mvn -f $JAVA_CODE_PATH/SeerLogicsBotStateMachine/pom.xml  clean install

mvn -f $JAVA_CODE_PATH/SeerlogicsBotAdmin/pom.xml  clean install

mvn -f $JAVA_CODE_PATH/SeerLogicsSharedBot/pom.xml  clean install