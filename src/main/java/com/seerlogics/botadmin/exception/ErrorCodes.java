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
}
