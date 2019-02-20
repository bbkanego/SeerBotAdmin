package com.seerlogics.botadmin.exception;

import opennlp.tools.util.InsufficientTrainingDataException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by bkane on 2/20/19.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends com.lingoace.common.GlobalExceptionHandler {
    // define the catch all here!!!
    @ExceptionHandler(Exception.class)
    public Object resolveException(HttpServletRequest httpServletRequest, Exception e) {
        Map<String, String> errorResponse = this.convertException(e);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (errorResponse.keySet().contains("errorCode")) {
            // this is a known error
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(convertException(e), httpStatus);
    }

    private Map<String, String> buildKnownErrorCodeAndMessageResponse(String errorCode,
                                                                      String errorMessage, Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        String errorId = UUID.randomUUID().toString();
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("errorMessage", errorMessage);
        errorResponse.put("referenceCode", errorId);

        LOGGER.error("Known error occurred. ReferenceId = " + errorId +
        "\n=================================================\n"
        + ExceptionUtils.getStackTrace(e) +
        "\n=================================================\n");

        return errorResponse;
    }

    private Map<String, String> buildUnknownErrorMessageResponse(Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        String errorId = UUID.randomUUID().toString();
        errorResponse.put("errorMessage", "There was a problem processing your request");
        errorResponse.put("referenceCode", errorId);

        LOGGER.error("Unknown error occurred. ReferenceId = " + errorId +
                "\n=================================================\n"
                + ExceptionUtils.getStackTrace(e) +
                "\n=================================================\n");

        return errorResponse;
    }

    private Map<String, String> convertException(Exception e) {
        if (e instanceof com.lingoace.exception.nlp.TrainModelException) {
            com.lingoace.exception.nlp.TrainModelException trainModelException = (com.lingoace.exception.nlp.TrainModelException)e;
            if (trainModelException.getCause() instanceof InsufficientTrainingDataException) {
                return buildKnownErrorCodeAndMessageResponse(ErrorCodes.TRAIN_MODEL_INSUFFICIENT_DATA,
                                            "Insufficient training data to create model", e);
            } else {
                return buildUnknownErrorMessageResponse(e);
            }
        } else {
            return buildUnknownErrorMessageResponse(e);
        }
    }
}
