package com.seerlogics.botadmin.exception;

import com.seerlogics.commons.exception.BaseRuntimeException;
import opennlp.tools.util.InsufficientTrainingDataException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.seerlogics.botadmin.exception.ErrorCodes.CONSTRAINT_VIOLATION_ERROR;
import static com.seerlogics.botadmin.exception.ErrorCodes.DATA_INTEGRITY_VIOLATION_ERROR;

/**
 * Created by bkane on 2/20/19.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends com.lingoace.common.GlobalExceptionHandler {

    public static final String MESSAGE = "message";
    @Resource(name = "appMessageResource")
    private MessageSource messageSource;

    @ExceptionHandler(BadCredentialsException.class)
    public Object handleValidationException(BadCredentialsException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(MESSAGE, "Invalid user name or password");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @Override
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        super.handleDataIntegrityViolationException(e);

        if (e.getCause() instanceof ConstraintViolationException) {
            return new ResponseEntity<>(buildKnownErrorCodeAndMessageResponse(CONSTRAINT_VIOLATION_ERROR
                    , messageSource.getMessage("CONSTRAINT_VIOLATION_ERROR_msg", null, null), e),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(buildKnownErrorCodeAndMessageResponse(DATA_INTEGRITY_VIOLATION_ERROR
                , messageSource.getMessage("DATA_INTEGRITY_VIOLATION_ERROR_msg", null, null), e),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleValidationException(ConstraintViolationException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(MESSAGE, "ConstraintViolationException");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object accessDeniedException(AccessDeniedException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(MESSAGE, "You are not authorized to access this resource");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // define the catch all here!!!
    @Override
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
        errorResponse.put("errorMessage", messageSource.getMessage("error_500_message", new Object[]{errorId}, null));
        errorResponse.put("referenceCode", errorId);

        LOGGER.error("Unknown error occurred. ReferenceId = " + errorId +
                "\n=================================================\n"
                + ExceptionUtils.getStackTrace(e) +
                "\n=================================================\n");

        return errorResponse;
    }

    private Map<String, String> convertException(Exception e) {
        if (e instanceof com.lingoace.exception.nlp.TrainModelException) {
            com.lingoace.exception.nlp.TrainModelException trainModelException =
                    (com.lingoace.exception.nlp.TrainModelException) e;
            if (trainModelException.getCause() instanceof InsufficientTrainingDataException) {
                return buildKnownErrorCodeAndMessageResponse(ErrorCodes.TRAIN_MODEL_INSUFFICIENT_DATA,
                        "Insufficient training data to create model", e);
            } else {
                return buildUnknownErrorMessageResponse(e);
            }
        } else if (e instanceof BaseRuntimeException) {
            return buildKnownErrorCodeAndMessageResponse(((BaseRuntimeException) e).getErrorCode(),
                    "This will get message drom DB", e);
        } else {
            return buildUnknownErrorMessageResponse(e);
        }
    }
}
