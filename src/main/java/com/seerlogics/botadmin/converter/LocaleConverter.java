package com.seerlogics.botadmin.converter;

import javax.persistence.AttributeConverter;
import java.util.Locale;

/**
 * Created by bkane on 11/11/18.
 */
public class LocaleConverter implements AttributeConverter<Locale, String> {
    @Override
    public String convertToDatabaseColumn(Locale locale) {
        if (locale != null) {
            return locale.toLanguageTag();
        }
        return null;
    }

    @Override
    public Locale convertToEntityAttribute(String languageTag) {
        if (languageTag != null && !languageTag.isEmpty()) {
            return Locale.forLanguageTag(languageTag);
        }
        return null;
    }
}
