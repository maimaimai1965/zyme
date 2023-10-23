package ua.mai.zyme.graphql.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ua.mai.zyme.db.config.JdbcConfigurationDefault;

/**
 * <pre>
 * Конфигурация для этого модуля <i>graphql-zoo</i>.
 * Если модуль включается в приложение, то в аннотации <i>@SpringBootApplication</i> в <i>scanBasePackageClasses</i>
 * должен быть указан этот класс.
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
        "ua.mai.zyme.graphql.controllers",
//        "ua.mai.zyme.graphql.zoo.services",
})
@Import({
        JdbcConfigurationDefault.class,
        GraphqlLearnSecurityConfig.class,
//        AuthenticationAuthorizationHandler.class
})
public class GraphqlLearnConfig {
}
