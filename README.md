# BotAdmin

## Available Profiles
1. Local DEV
This profile allows you run the Bot locally in dev mode and deploy the bot locally and test it.
2. AWS/Production
This profile allows you to deploy the Bot on AWS EC2 instance and test it.

## Possible Errors
```
Error Desc: Caused by: org.hibernate.PersistentObjectException: detached entity passed to persist: com.paulsanwald.Account
                at org.hibernate.event.internal.DefaultPersistEventListener.onPersist(DefaultPersistEventListener.java:141)

Soln: This is because you are passing an already saved/persisted entity as a possible FK into another object and then trying to "PERSIST" it
as if its a new entity. To resolve this issue remove the "CascadeType.PERSIST" from the cascade type list
```