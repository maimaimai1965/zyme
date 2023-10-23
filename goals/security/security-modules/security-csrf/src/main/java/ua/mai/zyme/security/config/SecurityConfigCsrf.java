package ua.mai.zyme.security.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ua.mai.zyme.security.adapters.CsrfWebSecurityConfigurerAdapter;
import ua.mai.zyme.security.controllers.LoginController;
import ua.mai.zyme.security.handlers.CustomAuthenticationFailureHandler;
import ua.mai.zyme.security.handlers.CustomAuthenticationSuccessHandler;

/**
 * <pre>
 * Конфигурация для CSRF security (используется в приложении <i>SecurityCsrfApplication</i>).
 * Если модуль включается в приложение, то в аннотации <i>@SpringBootApplication</i> в <i>scanBasePackageClasses</i>
 * должен быть указан этот класс.
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
      "ua.mai.zyme.security.controllers",
      "ua.mai.zyme.security.handlers"
})
@Import({
        CsrfWebSecurityConfigurerAdapter.class,
//        CustomAuthenticationSuccessHandler.class,
//        CustomAuthenticationFailureHandler.class,
//        LoginController.class
})
public class SecurityConfigCsrf {
}
