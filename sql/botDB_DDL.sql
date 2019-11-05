create table ACTION
(
    ID               BIGINT       not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    CODE             VARCHAR(50)  not null
        unique,
    DESCRIPTION      VARCHAR(200) not null,
    NAME             VARCHAR(50)  not null
        unique
);

create table CONTACT_MODE
(
    ID               BIGINT       not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    TYPE             VARCHAR(255) not null
);

create table ADDRESS
(
    CITY   VARCHAR(100) not null,
    STATE  VARCHAR(100) not null,
    STREET VARCHAR(200) not null,
    ZIP    VARCHAR(100) not null,
    ID     BIGINT       not null
        primary key,
    constraint FKHEHWBXYBNQYQITHVLTXGNSWFB
        foreign key (ID) references CONTACT_MODE
);

create table EMAIL
(
    EMAIL     VARCHAR(100) not null,
    EMAILTYPE VARCHAR(50)  not null,
    ID        BIGINT       not null
        primary key,
    constraint FKL5HEYEUPT03VB04KGJD9I4RYH
        foreign key (ID) references CONTACT_MODE
);

create table LANGUAGE
(
    ID               BIGINT       not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    CODE             VARCHAR(50)  not null,
    DESCRIPTION      VARCHAR(200) not null,
    LOCALE           VARCHAR(5)   not null,
    NAME             VARCHAR(50)  not null
);

create table PARTY
(
    ID               BIGINT       not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    NAME             VARCHAR(200) not null,
    TYPE             VARCHAR(255) not null
);

create table ACCOUNT
(
    ID               BIGINT                     not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    PASSWORD         VARCHAR(255)               not null,
    USER_NAME        VARCHAR(255)               not null
        unique,
    OWNER_ID         BIGINT                     not null,
    ENABLED          BOOLEAN     default TRUE   not null,
    REALM            VARCHAR(50) default 'seer' not null,
    constraint FKPYD0RL199DPMV884IYETT62MQ
        foreign key (OWNER_ID) references PARTY
);

create table CATEGORY
(
    ID               BIGINT            not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    CODE             VARCHAR(50)       not null
        unique,
    DESCRIPTION      VARCHAR(3000),
    NAME             VARCHAR(255)      not null
        unique,
    OWNER_ACCOUNT_ID BIGINT default 10 not null,
    constraint CATEGORY_ACCOUNT_ID_FK
        foreign key (OWNER_ACCOUNT_ID) references ACCOUNT,
    constraint FKGO7N7YGPN7CS4YBGJXTUCXAOM
        foreign key (OWNER_ACCOUNT_ID) references ACCOUNT
);

create table INTENT
(
    ID               BIGINT       not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    INTENT           VARCHAR(150) not null,
    INTENT_TYPE      VARCHAR(20)  not null,
    CATEGORY_ID      BIGINT       not null,
    OWNER_ACCOUNT_ID BIGINT       not null,
    MAY_BE_INTENT    BIGINT
        references INTENT,
    unique (INTENT, OWNER_ACCOUNT_ID),
    constraint FK57BYAVLFVT2BFBIETG4AA5S30
        foreign key (OWNER_ACCOUNT_ID) references ACCOUNT,
    constraint FK5BI9YWEYSSC2LCGURNAF29PXA
        foreign key (MAY_BE_INTENT) references INTENT,
    constraint FKCKREHPDT00IH5E3C4FI50G03J
        foreign key (CATEGORY_ID) references CATEGORY
);

create table INTENT_RESPONSE
(
    ID               BIGINT        not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    LOCALE           VARCHAR(8)    not null,
    RESPONSE         VARCHAR(3000) not null,
    RESPONSE_TYPE    VARCHAR(20)   not null,
    OWNER_INTENT_ID  BIGINT        not null,
    unique (RESPONSE, OWNER_INTENT_ID, LOCALE),
    constraint FK3JKE4GJUY0KX2R86TFU7BCNLD
        foreign key (OWNER_INTENT_ID) references INTENT
);

create table INTENT_UTTERANCE
(
    ID               BIGINT       not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    LOCALE           VARCHAR(8)   not null,
    UTTERANCE        VARCHAR(300) not null,
    OWNER_INTENT_ID  BIGINT       not null,
    unique (UTTERANCE, OWNER_INTENT_ID, LOCALE),
    constraint FKQ1G74OA83UEVG5Q9VB5H3JXE4
        foreign key (OWNER_INTENT_ID) references INTENT
);

create table ORGANIZATION
(
    DBA VARCHAR(200) not null,
    ID  BIGINT       not null
        primary key,
    constraint FKAILB6LQW031H08OFIA5XHHLCP
        foreign key (ID) references PARTY
);

create table PARTY_CONTACT_MODE
(
    PARTY_ID        BIGINT not null,
    CONTACT_MODE_ID BIGINT not null
        unique,
    primary key (PARTY_ID, CONTACT_MODE_ID),
    constraint FK6JCK4Q0O20OK2NCLKR35J17SJ
        foreign key (CONTACT_MODE_ID) references CONTACT_MODE,
    constraint FKHPASKGI0W7N6IG27N9OKWT3O7
        foreign key (PARTY_ID) references PARTY
);

create table PERSON
(
    FIRST_NAME VARCHAR(200) not null,
    LAST_NAME  VARCHAR(200) not null,
    ID         BIGINT       not null
        primary key,
    constraint FK1GQONTWML1QDNT2JSH1FOSOMD
        foreign key (ID) references PARTY
);

create table POLICY
(
    ID               BIGINT       not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    CODE             VARCHAR(50)  not null
        unique,
    DESCRIPTION      VARCHAR(200) not null,
    NAME             VARCHAR(50)  not null
        unique
);

create table RESOURCE
(
    ID               BIGINT       not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    CODE             VARCHAR(50)  not null
        unique,
    DESCRIPTION      VARCHAR(200) not null,
    NAME             VARCHAR(50)  not null
        unique
);

create table ROLE
(
    ID               BIGINT       not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    CODE             VARCHAR(50)  not null
        unique,
    DESCRIPTION      VARCHAR(1000),
    ENTITLEMENTS     VARCHAR(200) not null,
    ROLE             VARCHAR(50)  not null
        unique
);

create table ACCOUNT_ROLE
(
    ACCOUNT_ID BIGINT not null,
    ROLE_ID    BIGINT not null,
    primary key (ACCOUNT_ID, ROLE_ID),
    constraint FK1F8Y4IY71KB1ARFF79S71J0DH
        foreign key (ACCOUNT_ID) references ACCOUNT,
    constraint FKRS2S3M3039H0XT8D5YHWBUYAM
        foreign key (ROLE_ID) references ROLE
);

create table ROLE_POLICY
(
    ROLE_ID   BIGINT not null,
    POLICY_ID BIGINT not null,
    primary key (ROLE_ID, POLICY_ID),
    constraint FKI6044I230QY7VRJTYT90CIGBG
        foreign key (ROLE_ID) references ROLE,
    constraint FKLJVOSK9H4LY7QOOVI5R95KS92
        foreign key (POLICY_ID) references POLICY
);

create table STATEMENT
(
    ID               BIGINT        not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    CODE             VARCHAR(50)   not null
        unique,
    DESCRIPTION      VARCHAR(2000) not null,
    NAME             VARCHAR(200)  not null
        unique,
    EFFECT           VARCHAR(255),
    OWNER_ID         BIGINT,
    RESOURCE_ID      BIGINT        not null,
    constraint FKAFX627T8MIYHGJ51WJ4M0GTST
        foreign key (OWNER_ID) references POLICY,
    constraint FKPLNL2VJWKJIR1USQOXTQORVD1
        foreign key (RESOURCE_ID) references RESOURCE
);

create table STATEMENT_ACTION
(
    STATEMENT_ID BIGINT not null,
    ACTION_ID    BIGINT not null,
    primary key (STATEMENT_ID, ACTION_ID),
    constraint FKMEVRWWD1WJHDG61VELD7KV90D
        foreign key (STATEMENT_ID) references STATEMENT,
    constraint FKQX58WEXJNQ7T4M9AP3P9EN579
        foreign key (ACTION_ID) references ACTION
);

create table STATUS
(
    ID               BIGINT      not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    CODE             VARCHAR(50) not null
        unique,
    DESCRIPTION      VARCHAR(1000),
    NAME             VARCHAR(50) not null
        unique
);

create table BOT
(
    ID               BIGINT       not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    DESCRIPTION      VARCHAR(3000),
    DISPLAY_NAME     VARCHAR(100) not null,
    NAME             VARCHAR(100) not null
        unique,
    TYPE             VARCHAR(255) not null,
    CATEGORY_ID      BIGINT       not null,
    OWNER_ACCOUNT_ID BIGINT       not null,
    STATUS_ID        BIGINT       not null,
    constraint FKBO94RUD75S0NYUW4PXEHXCT18
        foreign key (OWNER_ACCOUNT_ID) references ACCOUNT,
    constraint FKBSFGN7KF1C88IQ8NI3PNXAJK8
        foreign key (CATEGORY_ID) references CATEGORY,
    constraint FKGEGXXOF301HP3952UMU83WLMD
        foreign key (STATUS_ID) references STATUS
);

create table BOT_LANGUAGE
(
    BOT_ID      BIGINT not null,
    LANGUAGE_ID BIGINT not null,
    primary key (BOT_ID, LANGUAGE_ID),
    constraint FK1UEGHK2KPQQJXTK4Y77IL38EC
        foreign key (BOT_ID) references BOT,
    constraint FKH1PBTWQ92GXXJP0D7VRLDXCBM
        foreign key (LANGUAGE_ID) references LANGUAGE
);

create table CHAT_BOT
(
    ID BIGINT not null
        primary key,
    constraint FKFGNO4O9P9MKPQGUR2C9E57CA0
        foreign key (ID) references BOT
);

create table TIER
(
    ID               BIGINT        not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    CODE             VARCHAR(50)   not null,
    DESCRIPTION      VARCHAR(2000) not null,
    NAME             VARCHAR(200)  not null,
    MAXTRANSACTIONS  BIGINT,
    MINTRANSACTIONS  BIGINT,
    TIERDURATION     VARCHAR(255)
);

create table TRAINED_MODEL
(
    ID               BIGINT        not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    DESCRIPTION      VARCHAR(1000) not null,
    FILE             BLOB,
    FILE_TYPE        VARCHAR(100)  not null,
    NAME             VARCHAR(300)  not null
        unique,
    TYPE             VARCHAR(20)   not null,
    CATEGORY_ID      BIGINT        not null,
    OWNER_ACCOUNT_ID BIGINT        not null,
    constraint FK2CP7A2M7F4UXRPQMCT6JEOWR4
        foreign key (CATEGORY_ID) references CATEGORY,
    constraint FKE70WICSOVT9RBE2C63PMNP3VW
        foreign key (OWNER_ACCOUNT_ID) references ACCOUNT
);

create table CONFIGURATION
(
    ID                 BIGINT not null
        primary key,
    CREATION_DATE      TIMESTAMP(26, 6),
    LAST_UPDATE_DATE   TIMESTAMP(26, 6),
    VERSION            BIGINT,
    ENVIRONMENT        VARCHAR(50),
    IMAGE_IDS          VARCHAR(500),
    INSTANCE_IDS       VARCHAR(500),
    LOAD_BALANCER_NAME VARCHAR(100),
    PORT               INTEGER,
    PUBLIC_DNS         VARCHAR(500),
    PUBLIC_IPS         VARCHAR(500),
    URL                VARCHAR(300),
    WORKING_FOLDER     VARCHAR(500),
    MODEL_ID           BIGINT not null,
    ALLOWED_ORIGINS    VARCHAR(500),
    UNIQUE_BOT_ID      VARCHAR(50),
    constraint FK75XPP2IO2B5NO92VPMT3AWNNX
        foreign key (MODEL_ID) references TRAINED_MODEL
);

create table BOT_CONFIGURATION
(
    BOT_ID    BIGINT not null,
    CONFIG_ID BIGINT not null
        unique,
    primary key (BOT_ID, CONFIG_ID),
    constraint FKK7077FATN2XQMLUM7M7J0FISM
        foreign key (BOT_ID) references BOT,
    constraint FKKAPBK9EOHXBBMUOHSXAYQRXFI
        foreign key (CONFIG_ID) references CONFIGURATION
);

create table LAUNCH_INFO
(
    ID               BIGINT not null
        primary key,
    CREATION_DATE    TIMESTAMP(26, 6),
    LAST_UPDATE_DATE TIMESTAMP(26, 6),
    VERSION          BIGINT,
    ALLOWED_ORIGINS  VARCHAR(100),
    CHAT_URL         VARCHAR(100),
    TARGET_BOT_ID    BIGINT,
    UNIQUE_BOT_ID    VARCHAR(50),
    MODEL_ID         BIGINT not null,
    constraint FK9OF0E5B2OCC2JI5WU5X39PTI3
        foreign key (MODEL_ID) references TRAINED_MODEL
);

create table BOT_LAUNCHINFO
(
    BOT_ID         BIGINT not null,
    LAUNCH_INFO_ID BIGINT not null
        unique,
    primary key (BOT_ID, LAUNCH_INFO_ID),
    constraint FK1XIT4HHHQNQBFDRJHEPSGFO3K
        foreign key (LAUNCH_INFO_ID) references LAUNCH_INFO,
    constraint FKQ3H1S76JCLWYQNRMKMFSSPAHB
        foreign key (BOT_ID) references BOT
);

create table VOICE_BOT
(
    ID BIGINT not null
        primary key,
    constraint FKNI2WOKSQ07W07YPWRGN6SK7M5
        foreign key (ID) references BOT
);