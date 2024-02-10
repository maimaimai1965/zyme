package ua.mai.zyme.graphql.config;

import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQlConfig {
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
//                .scalar(ExtendedScalars.Date)              // java.time.LocalDate
//                .scalar(ExtendedScalars.LocalTime)         // java.time.LocalTime
//                .scalar(ExtendedScalars.DateTime)          // java.time.OffsetDateTime
//                .scalar(ExtendedScalars.Time);             // java.time.OffsetTime
                .scalar(ExtendedScalars.GraphQLChar)       // Character
                .scalar(ExtendedScalars.GraphQLByte)       // Byte
                .scalar(ExtendedScalars.GraphQLShort)      // Short
                .scalar(ExtendedScalars.GraphQLLong)       // Long
                .scalar(ExtendedScalars.GraphQLBigInteger) // BigInteger
                .scalar(ExtendedScalars.GraphQLBigDecimal) // BigDecimal
                .scalar(ExtendedScalars.Currency)          // Currency
                .scalar(ExtendedScalars.Locale)            // Locale
                .scalar(ExtendedScalars.Url)
                .scalar(ExtendedScalars.UUID)
//                .scalar(ExtendedScalars.Object)
//                .scalar(ExtendedScalars.Json)
                ;
    }

}