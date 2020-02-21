create schema seerBotAdminDB collate utf8_unicode_ci;

create table ACCOUNT
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    ENABLED bit null,
    PASSWORD varchar(255) not null,
    REALM varchar(255) not null,
    USER_NAME varchar(255) not null,
    OWNER_ID bigint not null,
    constraint acc_unique_user_name
        unique (USER_NAME)
);

create index FKcs4201hx8xauajg34eubgwwey
    on ACCOUNT (OWNER_ID);

create table ACCOUNT_ROLE
(
    ACCOUNT_ID bigint not null,
    ROLE_ID bigint not null,
    primary key (ACCOUNT_ID, ROLE_ID)
);

create index FK7kmhvct13ewxr9d2e0by495hv
    on ACCOUNT_ROLE (ROLE_ID);

create table ACTION
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(2000) not null,
    NAME varchar(200) not null,
    constraint UK_35d4v36g0ugnp9k8wfviwid6a
        unique (CODE),
    constraint act_unique_code
        unique (CODE),
    constraint act_unique_name
        unique (NAME)
);

create table ADDRESS
(
    CITY varchar(100) not null,
    STATE varchar(100) not null,
    STREET varchar(200) not null,
    ZIP varchar(100) not null,
    ID bigint not null
        primary key
);

create table BOT
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    DESCRIPTION varchar(3000) null,
    DISPLAY_NAME varchar(100) not null,
    NAME varchar(100) not null,
    TYPE varchar(255) not null,
    CATEGORY_ID bigint not null,
    OWNER_ACCOUNT_ID bigint not null,
    STATUS_ID bigint not null,
    constraint bot_unique_name
        unique (NAME)
);

create index FKm31vdfti05fav008jjj557fmb
    on BOT (STATUS_ID);

create index FKpif5w6ubai7lvwkx8hh1vho1o
    on BOT (OWNER_ACCOUNT_ID);

create index FKq88cwju8whmyiabek8up7tgg6
    on BOT (CATEGORY_ID);

create table BOT_CONFIGURATION
(
    BOT_ID bigint not null,
    CONFIG_ID bigint not null,
    primary key (BOT_ID, CONFIG_ID),
    constraint UK_lp168bv3rrib3gv06ccrwiy3h
        unique (CONFIG_ID)
);

create table BOT_LANGUAGE
(
    BOT_ID bigint not null,
    LANGUAGE_ID bigint not null,
    primary key (BOT_ID, LANGUAGE_ID)
);

create index FKbs996ggqvo6ca3s23pki2e0bp
    on BOT_LANGUAGE (LANGUAGE_ID);

create table BOT_LAUNCHINFO
(
    BOT_ID bigint not null,
    LAUNCH_INFO_ID bigint not null,
    primary key (BOT_ID, LAUNCH_INFO_ID),
    constraint UK_4xgyxwh8xhyfxdss97qej0kxx
        unique (LAUNCH_INFO_ID)
);

create table CATEGORY
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(2000) not null,
    NAME varchar(200) not null,
    OWNER_ACCOUNT_ID bigint not null,
    constraint UK_fxvqaj784kyku1albpxjdluk1
        unique (CODE),
    constraint cat_unique_code
        unique (CODE),
    constraint cat_unique_name
        unique (NAME)
);

create index FK2yyqdh1j1iq42d1rml2st9q8t
    on CATEGORY (OWNER_ACCOUNT_ID);

create table CHAT_BOT
(
    ID bigint not null
        primary key
);

create table CONFIGURATION
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    ALLOWED_ORIGINS varchar(500) null,
    ENVIRONMENT varchar(50) null,
    IMAGE_IDS varchar(500) null,
    INSTANCE_IDS varchar(500) null,
    LOAD_BALANCER_NAME varchar(100) null,
    PORT int null,
    PUBLIC_DNS varchar(500) null,
    PUBLIC_IPS varchar(500) null,
    UNIQUE_BOT_ID varchar(50) null,
    URL varchar(300) null,
    WORKING_FOLDER varchar(500) null,
    MODEL_ID bigint not null
);

create index FK15m9j6t67wyomfem258wrq18u
    on CONFIGURATION (MODEL_ID);

create table CONTACT_MODE
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    TYPE varchar(255) not null
);

create table EMAIL
(
    EMAIL varchar(100) not null,
    EMAILTYPE varchar(50) not null,
    ID bigint not null
        primary key
);

create table INTENT
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    COPY_OF_PREDEFINED_INTENT bigint null,
    INTENT varchar(150) not null,
    INTENT_TYPE varchar(20) not null,
    CATEGORY_ID bigint not null,
    MAY_BE_INTENT bigint null,
    OWNER_ACCOUNT_ID bigint not null,
    constraint unq_intent_per_account_and_category
        unique (INTENT, CATEGORY_ID, OWNER_ACCOUNT_ID)
);

create index FK1js09ufiyoc97o1phosdindb
    on INTENT (OWNER_ACCOUNT_ID);

create index FK7jbtgrcd0qd8stcmpsb4h3ij7
    on INTENT (CATEGORY_ID);

create index FK869x36ok64foxucejhxw8f84t
    on INTENT (MAY_BE_INTENT);

create table INTENT_RESPONSE
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    LOCALE varchar(8) not null,
    RESPONSE varchar(3000) not null,
    RESPONSE_TYPE varchar(20) not null,
    OWNER_INTENT_ID bigint not null
);

create index FKevx9c1bmhtl31qyj4m5abudim
    on INTENT_RESPONSE (OWNER_INTENT_ID);

create table INTENT_UTTERANCE
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    LOCALE varchar(8) not null,
    UTTERANCE varchar(300) not null,
    OWNER_INTENT_ID bigint not null,
    constraint unq_utterance_per_locale
        unique (UTTERANCE, OWNER_INTENT_ID, LOCALE)
);

create index FKtmeu9v0lv6w4apfnwf4f20yl7
    on INTENT_UTTERANCE (OWNER_INTENT_ID);

create table LANGUAGE
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(2000) not null,
    NAME varchar(200) not null,
    LOCALE varchar(5) not null,
    constraint UK_a9qsocdveyefpmhfkxnactghs
        unique (CODE)
);

create table LAUNCH_INFO
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    ALLOWED_ORIGINS varchar(100) null,
    CHAT_URL varchar(100) null,
    TARGET_BOT_ID bigint null,
    UNIQUE_BOT_ID varchar(50) null,
    MODEL_ID bigint not null
);

create index FK3qpbp3da2n3g8se9aqkf5ql5b
    on LAUNCH_INFO (MODEL_ID);

create table MEMBERSHIP_PLAN
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(2000) not null,
    NAME varchar(200) not null,
    DURATION int not null,
    PRICE decimal(19,2) not null,
    TIER_ID bigint null,
    constraint UK_c1chqnevsa7tfq50yvg81hry9
        unique (CODE)
);

create index FKc61dt1rt8pbdr236mnkv8rlke
    on MEMBERSHIP_PLAN (TIER_ID);

create table ORGANIZATION
(
    DBA varchar(200) not null,
    ID bigint not null
        primary key
);

create table PARTY
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    NAME varchar(200) not null,
    TYPE varchar(255) not null
);

create table PARTY_CONTACT_MODE
(
    PARTY_ID bigint not null,
    CONTACT_MODE_ID bigint not null,
    primary key (PARTY_ID, CONTACT_MODE_ID)
);

create index FK399cmdhgsr48t3y4hcck77ca2
    on PARTY_CONTACT_MODE (CONTACT_MODE_ID);

create table PAYMENT_HISTORY
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    AMOUNT decimal(19,2) not null,
    DATE datetime not null,
    PAYMENTREFERENCE varchar(255) not null,
    PAYMENTTYPE varchar(255) null,
    SUBSCRIPTION_ID bigint not null
);

create index FK78m994jvw8dul0j5tncgmjigw
    on PAYMENT_HISTORY (SUBSCRIPTION_ID);

create table PERSON
(
    FIRST_NAME varchar(200) not null,
    LAST_NAME varchar(200) not null,
    ID bigint not null
        primary key
);

create table POLICY
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(2000) not null,
    NAME varchar(200) not null,
    constraint UK_i6c8lxasu8x207jilhelsr5qi
        unique (CODE),
    constraint pol_unique_code
        unique (CODE),
    constraint pol_unique_name
        unique (NAME)
);

create table RESOURCE
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(2000) not null,
    NAME varchar(200) not null,
    constraint UK_7godtjdh1ly0hsaxjsg7y5dm0
        unique (CODE),
    constraint rsc_unique_code
        unique (CODE),
    constraint rsc_unique_name
        unique (NAME)
);

create table ROLE
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(1000) null,
    ENTITLEMENTS varchar(200) not null,
    ROLE varchar(50) not null,
    constraint role_unique_code
        unique (CODE),
    constraint role_unique_role
        unique (ROLE)
);

create table ROLE_POLICY
(
    ROLE_ID bigint not null,
    POLICY_ID bigint not null,
    primary key (ROLE_ID, POLICY_ID)
);

create index FKquvvtkem2bc3satip7at28e57
    on ROLE_POLICY (POLICY_ID);

create table STATEMENT
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(2000) not null,
    NAME varchar(200) not null,
    EFFECT varchar(255) null,
    OWNER_ID bigint null,
    RESOURCE_ID bigint not null,
    constraint UK_9u37lf661myrtrtc6vuleb7r
        unique (CODE),
    constraint smt_unique_code
        unique (CODE),
    constraint smt_unique_name
        unique (NAME)
);

create index FKabkmdkjmr108g6bloex55q8a0
    on STATEMENT (OWNER_ID);

create index FKqj8ky5b2y30njc9dy9r26n6yi
    on STATEMENT (RESOURCE_ID);

create table STATEMENT_ACTION
(
    STATEMENT_ID bigint not null,
    ACTION_ID bigint not null,
    primary key (STATEMENT_ID, ACTION_ID)
);

create index FKa249yxswxa8kqq3sreqkgsq48
    on STATEMENT_ACTION (ACTION_ID);

create table STATUS
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(2000) not null,
    NAME varchar(200) not null,
    constraint UK_ct9t9s16ph5yl2x1lpmv2xefi
        unique (CODE),
    constraint status_unique_code
        unique (CODE),
    constraint status_unique_name
        unique (NAME)
);

create table SUBSCRIPTION
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    ACCOUNT_ID bigint null,
    MEMBERSHIP_PLAN_ID bigint null
);

create index FKdvyvba5wydsjp8pwppc39gc5o
    on SUBSCRIPTION (MEMBERSHIP_PLAN_ID);

create index FKf1b9cm2pptqne9q9i7ahihyhw
    on SUBSCRIPTION (ACCOUNT_ID);

create table TIER
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    CODE varchar(50) not null,
    DESCRIPTION varchar(2000) not null,
    NAME varchar(200) not null,
    MAXTRANSACTIONS bigint null,
    MINTRANSACTIONS bigint null,
    TIERDURATION varchar(255) null,
    TIERTYPE varchar(255) null,
    constraint UK_4my6ls5cceuoj5jtt6oeq6bi5
        unique (CODE)
);

create table TRAINED_MODEL
(
    ID bigint auto_increment
        primary key,
    ACTIVE bit null,
    CREATION_DATE datetime null,
    END_DATE datetime null,
    LAST_UPDATE_DATE datetime null,
    START_DATE datetime null,
    VERSION bigint null,
    DESCRIPTION varchar(1000) not null,
    FILE longblob null,
    FILE_TYPE varchar(100) not null,
    NAME varchar(300) not null,
    TYPE varchar(20) not null,
    CATEGORY_ID bigint not null,
    OWNER_ACCOUNT_ID bigint not null,
    constraint model_unique_name
        unique (NAME)
);

create index FKfo4rbyxm1a6dwv14m46f9lkea
    on TRAINED_MODEL (OWNER_ACCOUNT_ID);

create index FKiwa53yqgsfe4jjsst9lvof8er
    on TRAINED_MODEL (CATEGORY_ID);

create table VOICE_BOT
(
    ID bigint not null
        primary key
);