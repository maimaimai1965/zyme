package ua.mai.zyme.soap.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import ua.mai.zyme.db.config.DbConfigOnlyDefault;
import ua.mai.zyme.soap.zoo.config.SoapZooConfig;
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
          SoapZooConfig.class,
  }, exclude = {
//        ErrorMvcAutoConfiguration.class,
  })
@Import({DbConfigOnlyDefault.class})
@EnableConfigurationProperties
 ({
//         LibServicesProperties.class
 })
public class SoapZooApplication {

    private static final Logger log = LoggerFactory.getLogger(SoapZooApplication.class);

    private final Environment env;

    public SoapZooApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
//        System.getProperties().setProperty("org.jooq.no-logo", "true");
        System.getProperties().setProperty("spring.application.name", "SOAP Zoo Services");
        SpringApplication app = new SpringApplication(SoapZooApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        try {
            Environment env = app.run(args).getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

