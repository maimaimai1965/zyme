package ua.mai.zyme.rest.zoo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ua.mai.zyme.rest.zoo.authentication.JwtBasicWebSecurityConfigurerAdapter;
import ua.mai.zyme.rest.zoo.components.AuthenticationAuthorizationHandler;

/**
 * <pre>
 * Конфигурация для этого модуля <i>rest-zoo</i>.
 * Если модуль включается в приложение, то в аннотации <i>@SpringBootApplication</i> в <i>scanBasePackageClasses</i>
 * должен быть указан этот класс.
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
        "ua.mai.zyme.rest.zoo.controllers",
        "ua.mai.zyme.rest.zoo.services",
//        "ua.mai.zyme.rest.zoo.components"
})
@Import({
        JwtBasicWebSecurityConfigurerAdapter.class,
        AuthenticationAuthorizationHandler.class
})
public class RestZooConfig {
}
