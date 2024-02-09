package ua.mai.zyme.graphql.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.Locale;

public record ScalarInGraphQL(
        // Стандартные типы.
        Integer intScalar,
        Float floatScalar,
        String stringScalar,
        Boolean booleanScalar,
        String idScalar,

        // Расширенные типы из graphql-datetime-spring-boot-starter.
        java.util.Date           date,
        java.time.LocalDate      localDate,
        java.time.LocalDateTime  localDateTime,
        java.time.LocalTime      localTime,
        java.time.OffsetDateTime offsetDateTime,
        java.time.YearMonth      yearMonth,
        java.time.Duration       duration,


        // Расширенные типы из graphql-java-extended-scalars:
//        Date dateScalar,
//        LocalDate dateScalar,
//        LocalTime localTimeScalar,
//        OffsetDateTime dateTimeScalar,
//        OffsetTime timeScalar,
        Character  charScalar,
        Byte       byteScalar,
        Short      shortScalar,
        Long       longScalar,
        BigInteger bigIntegerScalar,
        BigDecimal bigDecimalScalar,
        Currency   currencyScalar,
        Locale localeScalar,
        java.net.URL urlScalar,
        java.util.UUID uuidScalar
) {}
