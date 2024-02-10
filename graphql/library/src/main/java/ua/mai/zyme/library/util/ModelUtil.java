package ua.mai.zyme.library.util;

import java.util.Locale;

public interface ModelUtil {

    public static <T> T valueForUpdate(String fieldName, T oldValue, T newValue, Boolean valueSetNull) {
        if (newValue != null) {
            if (valueSetNull != null && valueSetNull)
                throw new RuntimeException("'" + fieldName + "' cannot be 'true' if '" + fieldName + "SetNull' contains value");
            else
                return newValue;
        } else {
            if (valueSetNull != null && valueSetNull)
                return null;
            else
                return oldValue;
        }
    }

    public static String valueForUpdate(String fieldName, String oldValue, Locale newValue, Boolean valueSetNull) {
        if (newValue != null) {
            if (valueSetNull != null && valueSetNull)
                throw new RuntimeException("'" + fieldName + "' cannot be 'true' if '" + fieldName + "SetNull' contains value");
            else
                return newValue.getLanguage().toString();
        } else {
            if (valueSetNull != null && valueSetNull)
                return null;
            else
                return oldValue;
        }
    }

}
