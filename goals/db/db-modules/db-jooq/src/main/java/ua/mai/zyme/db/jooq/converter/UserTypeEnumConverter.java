package ua.mai.zyme.db.jooq.converter;

import org.jooq.Converter;
import org.jooq.DataType;
import ua.mai.zyme.db.jooq.CharEnumConverter;

import java.util.Map;

import static org.jooq.impl.SQLDataType.CHAR;

/**
 * Конвертирует символ из БД в значение UserType.
 */
public class UserTypeEnumConverter extends CharEnumConverter<UserType> {

    // Экземпляр конвертера.
    public static final UserTypeEnumConverter USER_TYPE_ENUM_CONVERTER
            = new UserTypeEnumConverter();

    // Экземпляр конвертера для массива.
    public static final Converter<String[], UserType[]> USER_TYPE_ENUM_ARR_CONVERTER
            = USER_TYPE_ENUM_CONVERTER.forArrays();

    // Новый тип данных USERTYPE, который можно использовать как любой другой тип данных, определенный в SQLDataType.
    // В БД хранится как char, который считывается в тип String, а далее конвертируется в тип UserType.
    public static final DataType<UserType> USERTYPE
            = CHAR.asConvertedDataType(USER_TYPE_ENUM_CONVERTER);


    public UserTypeEnumConverter() {
        super(UserType.class, Map.of(
                UserType.INTERNAL, "I",
                UserType.EXTERNAL, "E",
                UserType.SYSTEM, "S"
        ));
    }

}
