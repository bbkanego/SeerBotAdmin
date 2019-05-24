# BotAdmin

## Available Profiles
1. Local DEV
This profile allows you run the Bot locally in dev mode and deploy the bot locally and test it.
2. AWS/Production
This profile allows you to deploy the Bot on AWS EC2 instance and test it.

## How to run application locally
1. Main class: com.seerlogics.botadmin.BotAdminApplication
2. Java args: -DdepProfile=dev -DconfigLoc=/opt/installs/tomcat/8.5.9 -Dspring.profiles.active=local

## How to run BotAdmin Application locally
```
java -jar -Dspring.profiles.active=local -DdepProfile=dev -DconfigLoc=/opt/installs/tomcat/8.5.9 seerlogics-bot-admin-1.0.0-SNAPSHOT.jar
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