# BotAdmin

## Available Profiles
1. Local DEV
This profile allows you run the Bot locally in dev mode and deploy the bot locally and test it.
2. AWS/Production
This profile allows you to deploy the Bot on AWS EC2 instance and test it.

## How to run application locally
1. Main class: com.seerlogics.botadmin.BotAdminApplication
2. Java args: -DdepProfile=dev -DconfigLoc=/opt/installs/tomcat/8.5.9 -Dspring.profiles.active=local

## How to run chatbot locally
```
java -jar -Dspring.profiles.active=local chatbot-0.0.1-SNAPSHOT.jar --seerchat.bottype=EVENT_BOT --seerchat.botOwnerId=2903
```

## Create a Run configuration to run the reference bot in Intellij
1. In Main class enter: com.seerlogics.chatbot.ChatbotApplication
2. In VM Arg enter: -Dspring.profiles.active=local
3. In Program args enter: --seerchat.bottype=EVENT_BOT --seerchat.botOwnerId=2903
4. In "Use classpath of module" select: "SeerlogicsReferenceBot" module

## Possible Errors
```
Error Desc: Caused by: org.hibernate.PersistentObjectException: detached entity passed to persist: com.paulsanwald.Account
                at org.hibernate.event.internal.DefaultPersistEventListener.onPersist(DefaultPersistEventListener.java:141)

Soln: This is because you are passing an already saved/persisted entity as a possible FK into another object and then trying to "PERSIST" it
as if its a new entity. To resolve this issue remove the "CascadeType.PERSIST" from the cascade type list
```