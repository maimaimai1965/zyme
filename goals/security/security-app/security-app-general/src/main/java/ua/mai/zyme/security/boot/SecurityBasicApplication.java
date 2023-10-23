package ua.mai.zyme.security.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import ua.mai.zyme.db.config.DbConfigOnlyDefault;
import ua.mai.zyme.security.config.SecurityConfigBasic;
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
        SecurityConfigBasic.class,  // Конфигурация для Basic security .
        DbConfigOnlyDefault.class        // Конфигурация для работы с БД схемой zyme.
})
@EnableConfigurationProperties
 ({
//         RestServicesProperties.class
 })
public class SecurityBasicApplication {

    private static final Logger log = LoggerFactory.getLogger(SecurityBasicApplication.class);

    private final Environment env;

    public SecurityBasicApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
//        System.getProperties().setProperty("org.jooq.no-logo", "true");
        System.getProperties().setProperty("spring.application.name", "Security General Application");
        SpringApplication app = new SpringApplication(SecurityBasicApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        try {
            Environment env = app.run(args).getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

