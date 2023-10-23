package ua.mai.zyme.graphql.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import ua.mai.zyme.db.config.DbConfigOnlyDefault;
import ua.mai.zyme.graphql.zoo.config.GraphqlZooConfig;
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
        GraphqlZooConfig.class,
//        DbTransactionConfig.class
  }, exclude = {
//        JooqAutoConfiguration.class
//        SecurityAutoConfiguration.class,
//        ErrorMvcAutoConfiguration.class,
//        QuartzAutoConfiguration.class,
//        MailSenderAutoConfiguration.class
  })
@Import({
        GraphqlZooConfig.class
//        DbConfigOnlyDefault.class
})
@EnableConfigurationProperties
 ({
//         RestServicesProperties.class
 })
//@EnableFeignClients(basePackages = {"ua.telesens.o320.pgw.fcs.clients"})
public class GraphqlZooApplication {

    private static final Logger log = LoggerFactory.getLogger(GraphqlZooApplication.class);

    private final Environment env;

    public GraphqlZooApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
//        System.getProperties().setProperty("org.jooq.no-logo", "true");
        System.getProperties().setProperty("spring.application.name", "GraphQL Zoo Application");
        SpringApplication app = new SpringApplication(GraphqlZooApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        try {
            Environment env = app.run(args).getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

