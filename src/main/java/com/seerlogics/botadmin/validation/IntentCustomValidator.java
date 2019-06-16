package com.seerlogics.botadmin.validation;

import com.lingoace.validation.ValidationResult;
import com.seerlogics.commons.model.Intent;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class IntentCustomValidator {
    public static ValidationResult validateBean(Object bean, MessageSource messageSource,
                                                ValidationResult validationResult) {
        Intent currentIntent = (Intent) bean;
        Intent mayBeIntent = currentIntent.getMayBeIntent();
        if (mayBeIntent == null) {
            validationResult.addPageLevelError(
                    messageSource.getMessage("res_at_least_one_maybeResponse",
                            new String[]{}, Locale.getDefault()));
        }
        return validationResult;
    }
}
