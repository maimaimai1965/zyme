package ua.mai.zyme.security.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ua.mai.zyme.security.adapters.BasicOrKeyWebSecurityConfigurerAdapter;
import ua.mai.zyme.security.authentication.AuthenticationAuthorizationHandler;
import ua.mai.zyme.security.controllers.HelloRestController;
import ua.mai.zyme.security.filters.StaticKeyAuthenticationFilter;

/**
 * <pre>
 * Конфигурация для basic security (используется в приложении <i>SecurityBasicApplication</i>).
 * Если модуль включается в приложение, то в аннотации <i@Import</i> должен быть указан этот класс.
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
//      "ua.mai.zyme.security.authentication",
//      "ua.mai.zyme.security.controllers",
//      "ua.mai.zyme.security.handlers"
})
@Import({
        BasicOrKeyWebSecurityConfigurerAdapter.class,
        StaticKeyAuthenticationFilter.class,
        HelloRestController.class,
        AuthenticationAuthorizationHandler.class
})
public class SecurityConfigBasic {
}
