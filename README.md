# BotAdmin

## Available Profiles
1. Local DEV (local) This profile allows you run the Bot locally in dev mode and deploy the bot locally and test it.
```
mvn clean install -P local
```

2. Local DEV-WAR (local) This profile allows you run the Bot locally in dev mode and deploy the bot on tomcat and test it.
```
mvn clean install -P local-war
```
Once built copy the WAR to "~/installs/apache-tomcat-9.0.21/webapps" and start tomcat (using ~/installs/apache-tomcat-9.0.21/bin/startup.sh) and test using URL:
```
http://localhost:8091/botadmin/actuator/info
```

I have changed the server.xml for tomcat 9 and added the following in HOST section
```
<Context path="/botadmin" docBase="seerlogics-bot-admin-1.0.0-SNAPSHOT"/>
```
I have also added additional parameters like below to HOST element
```
autoDeploy="false" deployOnStartup="false"
```
to make the app work with a new context path.

3. AWS/Production (aws-ec2) This profile allows you to deploy the Bot on AWS EC2 instance and test it.
```
mvn clean install -P aws-ec2
```
Once the build succeeds you should see 'spring.profiles.active=aws-ec2' set in the application.properties file.

4. AWS/Production WAR (aws-ec2-war) This profile allows you to deploy the Bot WAR file on AWS EC2 instance and test it.
```
mvn clean install -P aws-ec2-war
```

## How to run application locally
1. Main class: com.seerlogics.botadmin.BotAdminApplication
2. Java args: -DdepProfile=dev -DconfigLoc=/opt/installs/tomcat/8.5.9 -Dspring.profiles.active=local

## How to run BotAdmin Application locally

1. Start the DB server. This server will be common for both the admin and shared bot application
```
$> cd  ~/svn/code/java/SeerlogicsBotAdmin/scripts
$> ./runH2.sh

```

2. Next start the BotAdmin application using the idea run configurations:
```
SeerBotAdminApp:local-8091
```
and then the shared bot using:
```
SeerLogicsSharedBot:local-8099
```
Once the shared bot is running you can confirm that using:
```
http://localhost:8099/chatbot/actuator/info
http://localhost:8099/chatbot/actuator/health
```
You can also test using Postman collection located in:
```
~/svn/code/java/SeerlogicsBotAdmin/postman
```

## Cloud Deployments
1. When testing deploying on cloud run the ''BotAdminApplication-ec2:8091'' configuration.
2. You should launch the bot from the Bot Admin application and instance will be launched.
3. You can connect to instance using the below command:
```
ssh -i ~/svn/bhushan/theory/AWS/SeerLogics/keyPairs/bizBotAdminLogin.pem ec2-user@ec2-3-14-73-84.us-east-2.compute.amazonaws.com
```

## Possible Errors
1. JPA Error
```
Error Desc: Caused by: org.hibernate.PersistentObjectException: detached entity passed to persist: com.paulsanwald.Account
                at org.hibernate.event.internal.DefaultPersistEventListener.onPersist(DefaultPersistEventListener.java:141)
```
Soln: This is because you are passing an already saved/persisted entity as a possible FK into another object and then trying to "PERSIST" it
as if its a new entity. To resolve this issue remove the "CascadeType.PERSIST" from the cascade type list


2. AWS login issue:
```
The authenticity of host 'ec2-18-222-187-228.us-east-2.compute.amazonaws.com (18.222.187.228)' can't be established.
ECDSA key fingerprint is SHA256:qWtnrKiQwMzqimgVAHsiI6T0Rgx/YSsOtcmF7otQ/gY.
Are you sure you want to continue connecting (yes/no)? yes
Warning: Permanently added 'ec2-18-222-187-228.us-east-2.compute.amazonaws.com,18.222.187.228' (ECDSA) to the list of known hosts.
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@         WARNING: UNPROTECTED PRIVATE KEY FILE!          @
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
Permissions 0644 for '/home/bkane/svn/bhushan/theory/AWS/SeerLogics/keyPairs/bizBotAdminLogin.pem' are too open.
It is required that your private key files are NOT accessible by others.
This private key will be ignored.
Load key "/home/bkane/svn/bhushan/theory/AWS/SeerLogics/keyPairs/bizBotAdminLogin.pem": bad permissions
Permission denied (publickey).
```
To fix this run the below command:
```
chmod 0400 ~/svn/bhushan/theory/AWS/SeerLogics/keyPairs/*.pem
```

## Unit Testing and SonarQube
1. In order to generate code coverage report, JaCoCo is being used.
2. JaCoCo has been configured in the "BOM" xml file.
3. References:
    a. https://www.mkyong.com/maven/maven-jacoco-code-coverage-example/
    b. https://www.baeldung.com/jacoco
    c. https://thepracticaldeveloper.com/2016/02/06/test-coverage-analysis-for-your-spring-boot-app/

## Example Code for AWS Management
1. http://www.doublecloud.org/2016/03/amazon-web-service-java-sdk-tutorial-create-new-virtual-machine/
2. AWS Management code: https://github.com/neowu/cmn-project/blob/master/cmn/src/main/java/core/aws/task/ec2/CreateInstanceTask.java
3. Create Instance Task: https://www.programcreek.com/java-api-examples/?code=neowu/cmn-project/cmn-project-master/cmn/src/main/java/core/aws/task/ec2/CreateInstanceTask.java#
4. Create ELB: https://www.programcreek.com/java-api-examples/?code=neowu/cmn-project/cmn-project-master/cmn/src/main/java/core/aws/task/ec2/CreateInstanceTask.java#

## Admin/Actuator URLs
1. Health URL: http://localhost:8091/actuator/health
2. Info URL: http://localhost:8091/actuator/info

## Running BOT H2 DB in server mode
```
java -cp ~/installs/H2/h2-1.4.199.jar org.h2.tools.Server -web -webAllowOthers -tcp -tcpAllowOthers -baseDir /Users/bkane/svn/code/java/SeerlogicsBotAdmin/h2
```
The above will start the server print out the below in console:
```
TCP server running at tcp://23.202.231.166:9092 (others can connect)

Web Console server running at http://192.168.0.113:8082 (others can connect)
```

##### You can connect to the server both the Web app, IDE(DB client) and H2 console at the same time using URL:

Make sure that you select "Connection Type=Remote" if its not already selected for you based on the URLs below.

**BotAdminDB URL:**
```
jdbc:h2:tcp://localhost/~/svn/code/java/SeerlogicsBotAdmin/h2/botDB
```
**ChatBot DB URL:**
```
jdbc:h2:tcp://localhost/~/svn/code/java/SeerlogicsBotAdmin/h2/chatBotServerDB
```


## Deploy Bot Admin and Chat Bot on AWS

1. Run the below script to deploy BotAdmin Sever on AWS:
```
/Users/bkane/svn/code/java/SeerlogicsBotAdmin/docs/AWS/scripts/launchBotAdmin.sh
```

The above script will copy the Bot Admin WAR and run the instances. When the instances 
run the below script will get invoked on start up. This will install JDK, Tomcat etc on the 
newly created instances.
```
/Users/bkane/svn/code/java/SeerlogicsBotAdmin/docs/AWS/scripts/onLaunchScript.sh
```

## Install Gogs

1. Gogs service is installed in: /etc/systemd/system/gogs.service
2. If gogs is not working do this:


## How to restart gogs: (https://vitux.com/how-to-install-gogs-git-service-on-ubuntu/)

- First SSH to bubuntu using the below command:
```
    ssh bkane@bubuntu -p 821
    
    // this will ask for pwd. enter the simplest pwd ever.
```

- Next run the command
```
sudo systemctl start gogs
sudo systemctl enable gogs
```

- You can now check the status of gogs service with the following command:
```
sudo systemctl status gogs
```

## SQL Server admin
```
bkane@bubuntu:~/installs/killbill$ mysql -u root -p
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 226
Server version: 5.7.22-0ubuntu0.16.04.1 (Ubuntu)

// show all users
select * from mysql.user;
```

## SSH to bhushanhplinux
```
ssh bkane@bhushanhplinux -p 22
``` 

## Kill Bill Notes
I have downloaded the source for Killbill (killbill-0.21.2 - https://github.com/killbill/killbill/tree/killbill-0.21.2) and built it locally.
```
// downloaded and build Kill-bill in folder:
bkane@bubuntu:~/installs/killbill

// next build kill bill as follows:
bkane@bubuntu:~/installs/killbill$ mvn clean install -DskipTests
```

#### Add kill bill users
```
bkane@bubuntu:~/installs/killbill$ mysql -u root -p  [pwd=simplest pwd ever]

mysql>CREATE USER 'killbill'@'%' IDENTIFIED BY 'killbill';

mysql>CREATE DATABASE killbill;

// grant full access on DB and ALL its tables
mysql>GRANT ALL PRIVILEGES ON killbill.* TO 'killbill'@'%';
```

#### Create the Killbill database tables
```
// login as killbill user
mysql -u killbill -p

USE killbill;

// next run the query
source ~/installs/killbill/tenant/src/main/resources/org/killbill/billing/tenant/ddl.sql
source ~/installs/killbill/entitlement/src/main/resources/org/killbill/billing/entitlement/ddl.sql
source ~/installs/killbill/util/src/main/resources/org/killbill/billing/util/ddl.sql
source ~/installs/killbill/subscription/src/main/resources/org/killbill/billing/subscription/ddl.sql
source ~/installs/killbill/catalog/src/main/resources/org/killbill/billing/catalog/ddl.sql
source ~/installs/killbill/payment/src/main/resources/org/killbill/billing/payment/ddl.sql
source ~/installs/killbill/beatrix/src/main/resources/org/killbill/billing/beatrix/ddl.sql
source ~/installs/killbill/account/src/main/resources/org/killbill/billing/account/ddl.sql
source ~/installs/killbill/invoice/src/main/resources/org/killbill/billing/invoice/ddl.sql
source ~/installs/killbill/usage/src/main/resources/org/killbill/billing/usage/ddl.sql

// next check if DB tables where created
desc custom_fields;
desc service_broadcasts;
```

#### Update MySQL connection parameters in Kill bill with above username/pwd
Next make sure that you change the MySQL username and password set above in:
```
${killbillinstalldir}/profiles/killbill/src/main/resources/killbill-server.properties
```
By default the mysql pwd will be "root/root". Change that as required.

#### Set up a super user to make API calls. Refer: http://docs.killbill.io/0.20/user_management.html
```
Refer ~/installs/killbill/profiles/killbill/src/main/resources

//change/add a superuser
[users]
superadmin=superadmin123,root

```

#### Finally Start Killbill server after the above changes
```
bkane@bubuntu:~/installs/killbill$ ./bin/start-server -s

// logs
bkane@bubuntu:~/installs/killbill$ ./profiles/killbill/logs
```

## SeerBot Admin MySQL DB creation

```
mysql> create user 'bkane'@'%' identified by 'bkane';
Query OK, 0 rows affected (0.01 sec)

mysql> create database seerBotAdminDB;
Query OK, 1 row affected (0.00 sec)

mysql> create database seerBotDB;
Query OK, 1 row affected (0.00 sec)

mysql> grant all privileges on seerBotAdminDB.* to 'bkane'@'%';
Query OK, 0 rows affected (0.00 sec)

mysql> grant all privileges on seerBotDB.* to 'bkane'@'%';
Query OK, 0 rows affected (0.00 sec)

```