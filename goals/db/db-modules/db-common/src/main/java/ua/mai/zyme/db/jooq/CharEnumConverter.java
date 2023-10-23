package ua.mai.zyme.db.jooq;

import org.jooq.impl.EnumConverter;

import java.util.Map;

/**
 * Конвертирует символ из БД в значение Enum.
 */
public abstract class CharEnumConverter<U extends Enum<U>> extends EnumConverter<String, U> {
    public CharEnumConverter(Class<U> toType, Map<U, String> enumMap) {
        super(String.class, toType, enumMap::get);
        if (toType.getEnumConstants().length != enumMap.size()) {
            throw new IllegalArgumentException("Enumeration mapping is not complete");
        }
    }
}
