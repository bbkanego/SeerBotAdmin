
select * from INTENT_RESPONSE where OWNER_INTENT_ID = (select id from INTENT where id = 4428);
select * from INTENT_UTTERANCE where OWNER_INTENT_ID = (select id from INTENT where id = 4428);

select * from INTENT where id in (select MAY_BE_INTENT from
    INTENT where OWNER_ACCOUNT_ID = 4286) and OWNER_ACCOUNT_ID = 4286;

select * from intent where INTENT = 'MaybeFeaturedRecipes';

select * from intent where INTENT_TYPE = 'MAYBE' and OWNER_ACCOUNT_ID = 4286;
select id,intent,INTENT_TYPE,MAY_BE_INTENT from intent where OWNER_ACCOUNT_ID = 4286;

update intent ci
set MAY_BE_INTENT = (select id from intent where ci.INTENT_TYPE = 'MAYBE'
                                             and INTENT.INTENT = CONCAT('Maybe', ci.INTENT) and ci.OWNER_ACCOUNT_ID = 4286)
where ci.OWNER_ACCOUNT_ID = 4286;

select id from intent where INTENT_TYPE = 'MAYBE' and INTENT.INTENT = CONCAT('Maybe', 'Hi') and OWNER_ACCOUNT_ID = 4286
