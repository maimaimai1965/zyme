package ua.mai.zyme.db.jooq.converter;

import org.jooq.Converter;
import org.jooq.DataType;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.jooq.impl.SQLDataType.LOCALDATETIME;


public class YearMonthToLocalDateTimeConverter implements Converter<LocalDateTime, YearMonth> {

    // Экземпляр конвертера.
    public static final Converter<LocalDateTime, YearMonth> YEARMONTH_TO_LOCALDATETIME_CONVERTER
            = new YearMonthToLocalDateTimeConverter();

    // Экземпляр конвертера для массива.
    public static final Converter<LocalDateTime[], YearMonth[]> YEARMONTH_TO_LOCALDATETIME_ARR_CONVERTER
            = YEARMONTH_TO_LOCALDATETIME_CONVERTER.forArrays();

    // Новый тип данных YEARMONTH, который можно использовать как любой другой тип данных, определенный в SQLDataType.
    // В БД хранится как timestamp, который считывается в тип LocalDateTime, а далее конвертируется в тип YearMonth.
    public static final DataType<YearMonth> YEARMONTH
            = LOCALDATETIME.asConvertedDataType(YEARMONTH_TO_LOCALDATETIME_CONVERTER);

    @Override
    public YearMonth from(LocalDateTime t) {
        if (t != null) {
            return YearMonth.from(t.toInstant(ZoneOffset.UTC)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate());
        }
        return null;
    }

    @Override
    public LocalDateTime to(YearMonth u) {
        if (u != null) {
            return u.atDay(1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        return null;
    }

    @Override
    public Class<LocalDateTime> fromType() {
        return LocalDateTime.class;
    }

    @Override
    public Class<YearMonth> toType() {
        return YearMonth.class;
    }

}
