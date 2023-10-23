package ua.mai.zyme.graphql.zoo.config;

import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import ua.mai.zyme.db.config.DbConfigOnlyDefault;

/**
 * <pre>
 * Конфигурация для этого модуля <i>graphql-zoo</i>.
 * Если модуль включается в приложение, то в аннотации <i>@SpringBootApplication</i> в <i>scanBasePackageClasses</i>
 * должен быть указан этот класс.
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
        "ua.mai.zyme.graphql.zoo.controllers",
        "ua.mai.zyme.graphql.zoo.services",
        "ua.mai.zyme.graphql.components"
})
@Import({
        DbConfigOnlyDefault.class,
        GraphZooSecurityConfig.class,
        GraphqlZooConfigCustom.class
//        AuthenticationAuthorizationHandler.class
})
public class GraphqlZooConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder ->
                wiringBuilder.scalar(ExtendedScalars.GraphQLLong)
                             .scalar(ExtendedScalars.DateTime)
                             .scalar(ExtendedScalars.Date)
//                             .scalar(ExtendedScalars.Time)
//                             .scalar(ExtendedScalars.LocalTime)
                             .scalar(ExtendedScalars.Json)
//                             .scalar(ExtendedScalars.NonNegativeInt)
                ;
    }

}
