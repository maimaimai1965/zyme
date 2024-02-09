package ua.mai.graphql.сontroller;

import lombok.SneakyThrows;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ua.mai.graphql.model.ScalarInGraphQL;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
class ScalarInGraphQLController {

    @SneakyThrows
    @QueryMapping
    public List<ScalarInGraphQL> scalarInGraphQLs() {
        return List.of(
                new ScalarInGraphQL(
                        // Стандартные типы.
                        Integer.valueOf(32000),
                        Float.valueOf(3.14f),
                        "да",
                        Boolean.TRUE,
                        "ID12345",
                        // Расширенные типы из graphql-datetime-spring-boot-starter.
                        new Date(2010, 1, 12),
                        LocalDate.of(2020, 2, 25),
                        LocalDateTime.of(2020, 2, 25, 13, 30, 00),
                        LocalTime.of(12, 5, 30),
                        OffsetDateTime.of(LocalDateTime.of(2010, 1, 12, 13, 30, 00), ZoneOffset.UTC),
                        YearMonth.of(2020, 3),
                        Duration.ofDays(15),
                        // Расширенные типы из graphql-java-extended-scalars:
                        Character.valueOf('Й'),
                        Byte.valueOf("100"),
                        Short.valueOf("2000"),
                        Long.valueOf(600000L),
                        BigInteger.valueOf(70000000L),
                        BigDecimal.valueOf(1234.12),
                        java.util.Currency.getInstance("USD"),
                        Locale.of("uk"),
                        new java.net.URL("https://github.com/graphql-java/graphql-java-extended-scalars"),
                        java.util.UUID.randomUUID()
                ),
                new ScalarInGraphQL(
                        // Стандартные типы.
                        Integer.valueOf(64000),
                        Float.valueOf(3.14f),
                        "ok",
                        Boolean.FALSE,
                        "ID555",
                        // Расширенные типы из graphql-datetime-spring-boot-starter.
                        new Date(2010, 1, 12),
                        LocalDate.of(2010, 1, 14),
                        LocalDateTime.of(2010, 2, 25, 13, 30, 00),
                        LocalTime.of(18, 30, 00),
                        OffsetDateTime.of(LocalDateTime.of(2010, 1, 12, 13, 30, 00), ZoneOffset.UTC),
                        YearMonth.of(2010, 9),
                        Duration.ofDays(30),
                        // Расширенные типы из graphql-java-extended-scalars:
                        Character.valueOf('Я'),
                        Byte.valueOf("101"),
                        Short.valueOf("2001"),
                        Long.valueOf(600001L),
                        BigInteger.valueOf(70000001L),
                        BigDecimal.valueOf(123456.123),
                        java.util.Currency.getInstance("USD"),
                        Locale.of("ru"),
                        new java.net.URL("https://github.com/graphql-java/graphql-java-extended-scalars"),
                        java.util.UUID.randomUUID()
                )
        );
    }

}
