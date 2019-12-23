#!/bin/sh

java -cp /opt/installs/H2/h2-1.4.199.jar org.h2.tools.Server -web -webAllowOthers -tcp -tcpAllowOthers -baseDir ~/svn/code/java/SeerlogicsBotAdmin/h2
