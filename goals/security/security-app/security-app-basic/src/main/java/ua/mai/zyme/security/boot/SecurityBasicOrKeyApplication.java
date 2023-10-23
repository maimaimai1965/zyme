package ua.mai.zyme.security.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import ua.mai.zyme.db.config.DbConfigOnlyDefault;
import ua.mai.zyme.security.adapters.BasicOrKeyWebSecurityConfigurerAdapter;
import ua.mai.zyme.security.authentication.AaJdbcUserDetailsManager;
import ua.mai.zyme.security.authentication.AuthenticationAuthorizationHandler;
import ua.mai.zyme.security.authentication.CustomPasswordEncoder;
import ua.mai.zyme.security.config.SecurityConfigBasic;
import ua.mai.zyme.security.controllers.HelloRestController;
import ua.mai.zyme.security.filters.AuthenticationLoggingFilter;
import ua.mia.zyme.common.DefaultProfileUtil;
import ua.mia.zyme.common.SpringUtil;

/**
 * <pre>
 * Приложение реализующее аутентификацию через Basic или Static Key.
 * Конфигурация Spring Security определена в {@link BasicOrKeyWebSecurityConfigurerAdapter}.
 *
 * Для Basic аутентификации используется:
 * - {@link AaJdbcUserDetailsManager}, который использует MySQL схему zyme.
 *       Используются таблицы <i>AA_USER, AA_ROLE, AA_USER_ROLE, AA_GROUP, AA_GROUP_ROLE, AA_GROUP_MEMBER</i>.
 *       Manager предоставляет авторизацию по ролям с использованием групп ролей.
 * - {@link CustomPasswordEncoder};
 * - {@link AuthenticationAuthorizationHandler} для обработки ошибок.
 *
 * Бизнес логика реализуется в:
 * - {@link HelloRestController}.
 *       Не используется доступ к методам по ролям.
 *
 * Для доступа к БД используется:
 * - {@link DbConfigOnlyDefault} - дефолтная спринговая конфигурация для MySQL схемы zyme.
 *
 * Для логирования используется:
 * - фильтр {@link AuthenticationLoggingFilter} - логирует информацию после аутентификации запроса;
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
        SecurityConfigBasic.class,  // Конфигурация для Basic security .
        DbConfigOnlyDefault.class   // Конфигурация для работы с БД схемой zyme.
})
@EnableConfigurationProperties
 ({
//         RestServicesProperties.class
 })
public class SecurityBasicOrKeyApplication {

    private static final Logger log = LoggerFactory.getLogger(SecurityBasicOrKeyApplication.class);

    private final Environment env;

    public SecurityBasicOrKeyApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
//        System.getProperties().setProperty("org.jooq.no-logo", "true");
        System.getProperties().setProperty("spring.application.name", "Security Basic or Key Application");
        SpringApplication app = new SpringApplication(SecurityBasicOrKeyApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        try {
            Environment env = app.run(args).getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

