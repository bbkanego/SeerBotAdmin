create schema seerBotDB collate utf8_unicode_ci;

use seerBotDB;

create table CHAT
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    ACCOUNT_ID varchar(255) null,
    AUTH_CODE varchar(255) null,
    CHAT_SESSION_ID varchar(255) not null,
    MESSAGE varchar(255) not null,
    OWNER_ACCOUNT_ID varchar(255) null,
    RESPONSE varchar(3000) null,
    PREVIOUS_CHAT_ID bigint null
);

create index FKklhkvnrqyh32jejao0a9hv5lm
    on CHAT (PREVIOUS_CHAT_ID);

use seerBotDB;
create table TRANSACTION
(
    ID               bigint auto_increment
        primary key,
    ACTIVE           bit              null,
    CREATION_DATE    datetime         null,
    END_DATE         datetime         null,
    LAST_UPDATE_DATE datetime         null,
    START_DATE       datetime         null,
    VERSION          bigint           null,
    ACCOUNT_ID       bigint           not null,
    INTENT           varchar(255)     not null,
    SUCCESS          bit              not null,
    TARGET_BOT_ID    bigint           not null,
    UTTERANCE        varchar(255)     not null,
    RESOLVED         bit              null,
    -- IGNORE is a reserved keyword and is thus quoted.
    IGNORE_TRANS         bit default b'0' null
);
