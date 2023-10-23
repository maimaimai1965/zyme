package ua.mai.zyme.security.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import ua.mai.zyme.db.config.JdbcConfigurationDefaultH2;
import ua.mai.zyme.security.adapters.JwtBasicWebSecurityConfigurerAdapter;
import ua.mai.zyme.security.authentication.AuthenticationAuthorizationHandler;
import ua.mai.zyme.security.authentication.H2JdbcUserDetailsManager;
import ua.mai.zyme.security.config.SecurityConfigJwtBasic;
import ua.mai.zyme.security.controllers.AuthController;
import ua.mai.zyme.security.controllers.HelloRestController;
import ua.mia.zyme.common.DefaultProfileUtil;
import ua.mia.zyme.common.SpringUtil;

/**
 * <pre>
 * Приложение реализующее аутентификацию через JWT токен, получаемый с использованием Basic аутентификации+подтверждение кодом.
 * Конфигурация Spring Security определена в {@link JwtBasicWebSecurityConfigurerAdapter}.
 *
 * Для Basic аутентификации используется:
 * - {@link H2JdbcUserDetailsManager}, который использует embedded H2 схему zyme03.
 *       Используются таблицы <i>USER, AUTHORITY</i>.
 *       Manager предоставляет авторизацию по ролям <b>без</b> использования групп ролей.
 * - {@link AuthController} - контроллер, получающий JWT токен для пользователя;
 * - {@link NoOpPasswordEncoder} - пароли при обработке не кодируются;
 * - TODO {@link AuthenticationAuthorizationHandler} для обработки ошибок.
 *
 * Бизнес логика реализуется в:
 * - {@link HelloRestController}.
 *       Используется доступ к методам по ролям.
 *
 * Для доступа к БД используется:
 * - {@link JdbcConfigurationDefaultH2} - дефолтная спринговая конфигурация для embedde H2 схемы zyme03.
 *
 * Для логирования используется:
 * - логер "Authentication", уровень логирования которого устанавливается в файле конфигурации приложения;
 * - {@link AuthenticationAuthorizationHandler} логирует ошибки.
 * </pre>
 */
@SpringBootApplication
  (scanBasePackageClasses = {
//        DbTransactionConfig.class
  }, exclude = {
//        UserDetailsServiceAutoConfiguration.class
//        SecurityAutoConfiguration.class,
//        JooqAutoConfiguration.class
//        ErrorMvcAutoConfiguration.class,
  })
@Import({
        SecurityConfigJwtBasic.class,     // Конфигурация для Basic security.
        JdbcConfigurationDefaultH2.class, // Конфигурация для работы с дефолтной спринговой схемой - zyme03 в БД embedded H2.
//        JdbcConfigurationZyme.class,      // Конфигурация для работы со схемой zyme в БД MySQL.
})
@EnableConfigurationProperties ({
//        RestServicesProperties.class
 })
public class SecurityJwtBasicApplication {

    private static final Logger log = LoggerFactory.getLogger(SecurityJwtBasicApplication.class);

    private final Environment env;

    public SecurityJwtBasicApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        System.getProperties().setProperty("spring.application.name", "Security GWT by Basic authentication  Application");
        SpringApplication app = new SpringApplication(SecurityJwtBasicApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        try {
            Environment env = app.run(args).getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

