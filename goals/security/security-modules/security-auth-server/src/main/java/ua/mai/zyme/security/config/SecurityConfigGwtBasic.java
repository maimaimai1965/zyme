package ua.mai.zyme.security.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ua.mai.zyme.security.adapters.AuthServerWebSecurityConfigurerAdapter;
import ua.mai.zyme.security.authentication.AuthenticationAuthorizationHandler;
import ua.mai.zyme.security.filters.JwtAuthenticationFilter;

/**
 * <pre>
 * Конфигурация для JWT security использующее начальную Basic аутентификацию (используется в приложении <i>SecurityGwtBasicApplication</i>).
 * Если модуль включается в приложение, то в аннотации <i@Import</i> должен быть указан этот класс.
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
      "ua.mai.zyme.security.controllers",
})
@Import({
        AuthServerWebSecurityConfigurerAdapter.class,
        JwtAuthenticationFilter.class,
//        HelloRestController.class,
        AuthenticationAuthorizationHandler.class
})
public class SecurityConfigGwtBasic {
}
