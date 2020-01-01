package com.seerlogics.botadmin.exception;

/**
 * Created by bkane on 2/16/19.
 */
public final class ErrorCodes {
    // NLP exceptions
    public static String SENT_MODEL_NOT_FOUND = "SBA-10001";
    public static String TOKENIZER_MODEL_NOT_FOUND = "SBA-10002";
    public static String MODEL_INITIALIZATION_ERROR = "SBA-10003";
    public static String TRAIN_MODEL_INSUFFICIENT_DATA = "SBA-10004";
    public static String INTENTS_UPLOAD_ERROR = "SBA-10005";
    public static String INTENTS_RESPONSE_NUM_ERROR = "SBA-10006";
    public static String DUPLICATE_INTENTS_RESPONSE_TYPE_ERROR = "SBA-10007";
    public static String UNAUTHORIZED_ACCESS = "SBA-10008";
    public static String CONSTRAINT_VIOLATION_ERROR = "SBA-10009";
    public static String DATA_INTEGRITY_VIOLATION_ERROR = "SBA-10010";
    public static String PREDEFINED_INTENTS_ALREADY_COPIED = "SBA-10011";
    public static String BOT_RE_INITIALIZATION_FAILED = "SBA-10012";
    public static String INTENT_ALREADY_ADDED = "SBA-10013";
    public static String INTENT_UTTERANCE_ALREADY_ADDED = "SBA-10014";
}
