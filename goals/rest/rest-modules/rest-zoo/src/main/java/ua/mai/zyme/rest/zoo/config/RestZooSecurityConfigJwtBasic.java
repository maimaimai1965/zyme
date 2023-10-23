package ua.mai.zyme.rest.zoo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ua.mai.zyme.rest.zoo.authentication.JwtAuthenticationFilter;
import ua.mai.zyme.rest.zoo.authentication.JwtBasicWebSecurityConfigurerAdapter;
import ua.mai.zyme.rest.zoo.components.AuthenticationAuthorizationHandler;

/**
 * <pre>
 * Конфигурация для JWT security использующее начальную Basic аутентификацию (используется в приложении <i>RestZooApplication</i>).
 * Если модуль включается в приложение, то в аннотации <i@Import</i> должен быть указан этот класс.
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
//      "ua.mai.zyme.security.controllers",
})
@Import({
        JwtBasicWebSecurityConfigurerAdapter.class,
        JwtAuthenticationFilter.class,
        AuthenticationAuthorizationHandler.class
})
public class RestZooSecurityConfigJwtBasic {
}
