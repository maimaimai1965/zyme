package ua.mia.zyme.common;

import java.lang.reflect.InvocationTargetException;

public final class EnumUtil {

    private EnumUtil() {
    }

    public static String getValueOfEnum(Enum<?> enumm) {
        if (enumm == null) return null;
        try {
            return enumm.getClass().getMethod("value").invoke(enumm).toString();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return enumm.toString();
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E getEnumFromValue(String value, Class<E> enumClass) {
        if (value == null)
            return null;
        try {
            return (E) enumClass.getMethod("fromValue", String.class).invoke(null, value);
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            try {
                return (E) enumClass.getMethod("valueOf", String.class).invoke(null, value);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                return null;
            }
        }
    }

}
