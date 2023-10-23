package ua.mai.zyme.rest.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import ua.mai.zyme.db.config.DbConfigOnlyDefault;
import ua.mai.zyme.rest.zoo.config.RestZooConfig;
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
        RestZooConfig.class,
//        DbTransactionConfig.class
  }, exclude = {
//        JooqAutoConfiguration.class
//        SecurityAutoConfiguration.class,
//        ErrorMvcAutoConfiguration.class,
//        QuartzAutoConfiguration.class,
//        MailSenderAutoConfiguration.class
  })
@Import({DbConfigOnlyDefault.class})
@EnableConfigurationProperties
 ({
//         RestServicesProperties.class
 })
//@EnableFeignClients(basePackages = {"ua.telesens.o320.pgw.fcs.clients"})
public class RestZooApplication {

    private static final Logger log = LoggerFactory.getLogger(RestZooApplication.class);

    private final Environment env;

    public RestZooApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
//        System.getProperties().setProperty("org.jooq.no-logo", "true");
        System.getProperties().setProperty("spring.application.name", "REST Library Services");
        SpringApplication app = new SpringApplication(RestZooApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        try {
            Environment env = app.run(args).getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

