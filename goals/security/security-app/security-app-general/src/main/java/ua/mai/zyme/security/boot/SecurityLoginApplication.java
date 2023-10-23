package ua.mai.zyme.security.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import ua.mai.zyme.db.config.DbConfigOnlyDefault;
import ua.mai.zyme.security.config.SecurityConfigLogin;
import ua.mia.zyme.common.DefaultProfileUtil;
import ua.mia.zyme.common.SpringUtil;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
  (scanBasePackageClasses = {
//        DbTransactionConfig.class
  }, exclude = {
//          SecurityAutoConfiguration.class,
//        JooqAutoConfiguration.class
//        ErrorMvcAutoConfiguration.class,
//        QuartzAutoConfiguration.class,
//        MailSenderAutoConfiguration.class
  })
@Import({
        DbConfigOnlyDefault.class,  // Конфигурация для работы с БД схемой zyme.
        SecurityConfigLogin.class
})
@EnableConfigurationProperties
 ({
//         RestServicesProperties.class
 })
public class SecurityLoginApplication {

    private static final Logger log = LoggerFactory.getLogger(SecurityLoginApplication.class);

    private final Environment env;

    public SecurityLoginApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
//        System.getProperties().setProperty("org.jooq.no-logo", "true");
        System.getProperties().setProperty("spring.application.name", "Security Login Application");
        SpringApplication app = new SpringApplication(SecurityLoginApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        try {
            Environment env = app.run(args).getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

