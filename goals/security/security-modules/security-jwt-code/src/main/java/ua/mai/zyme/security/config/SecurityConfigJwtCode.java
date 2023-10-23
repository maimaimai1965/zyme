package ua.mai.zyme.security.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ua.mai.zyme.security.adapters.JwtCodeWebSecurityConfigurerAdapter;
import ua.mai.zyme.security.authentication.AuthenticationAuthorizationHandler;
import ua.mai.zyme.security.filters.JwtAuthenticationFilter;

/**
 * <pre>
 * Конфигурация для JWT security использующее начальную Basic+Code аутентификацию (используется в приложении <i>SecurityGwtCodeApplication</i>).
 * Если модуль включается в приложение, то в аннотации <i@Import</i> должен быть указан этот класс.
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
      "ua.mai.zyme.security.controllers",
      "ua.mai.zyme.security.services",
      "ua.mai.zyme.security.repositories",
      "ua.mai.zyme.security.entities",
})
@Import({
        JwtCodeWebSecurityConfigurerAdapter.class,
        JwtAuthenticationFilter.class,
        AuthenticationAuthorizationHandler.class
})
public class SecurityConfigJwtCode {
}
