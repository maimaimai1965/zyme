package ua.mai.zyme.vaadin8.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import ua.mai.zyme.vaadin8.boot.config.Vaadin8UiConfiguration;
import ua.mai.zyme.vaadin8.db.config.JdbcConfigurationDefault;
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
                Vaadin8UiConfiguration.class,
                JdbcConfigurationDefault.class,
        }, exclude = {
//                ErrorMvcAutoConfiguration.class
        })
@EnableConfigurationProperties
        ({
//                AaClientProperties.class,
        })
//@Theme("custom-theme")
public class Vaadin8SimpleApplication {

    private static final Logger log = LoggerFactory.getLogger(Vaadin8SimpleApplication.class);

    private final Environment env;

    public Vaadin8SimpleApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Vaadin8SimpleApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        try {
            Environment env = app.run(args).getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
