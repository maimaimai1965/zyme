package ua.mai.zyme.graphql.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import ua.mai.zyme.graphql.config.GraphqlLearnConfig;
import ua.mai.zyme.graphql.learn.BookController;
//import ua.mai.zyme.db.config.DbConfigOnlyDefault;
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
        BookController.class,
        GraphqlLearnConfig.class,
//        DbTransactionConfig.class
  }, exclude = {
//        JooqAutoConfiguration.class
//        SecurityAutoConfiguration.class,
//        ErrorMvcAutoConfiguration.class,
//        QuartzAutoConfiguration.class,
//        MailSenderAutoConfiguration.class
  })
@Import({
//        DbConfigOnlyDefault.class
        }
)
@EnableConfigurationProperties
 ({
//         RestServicesProperties.class
 })
//@EnableFeignClients(basePackages = {"ua.telesens.o320.pgw.fcs.clients"})
public class GraphqlLearnApplication {

    private static final Logger log = LoggerFactory.getLogger(GraphqlLearnApplication.class);

    private final Environment env;

    public GraphqlLearnApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
//        System.getProperties().setProperty("org.jooq.no-logo", "true");
        System.getProperties().setProperty("spring.application.name", "GraphQL Learn Application");
        SpringApplication app = new SpringApplication(GraphqlLearnApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        try {
            Environment env = app.run(args).getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

