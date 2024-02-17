package ua.mai.zyme.graphql.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import ua.mai.zyme.graphql.config.GraphQlConfig;
import ua.mai.zyme.library.config.LibraryConfig;
import ua.mai.zyme.spring.log.LogConfig;
import ua.mia.zyme.common.DefaultProfileUtil;
import ua.mia.zyme.common.SpringUtil;

@SpringBootApplication(
        scanBasePackages = {
                "ua.mai.zyme.graphql",
                "ua.mai.zyme.library.repository"
        }
)
@Import({
        GraphQlConfig.class,
        LibraryConfig.class,
        LogConfig.class
})
public class GraphQLLearnApplication {

    private static final Logger log = LoggerFactory.getLogger(GraphQLLearnApplication.class);

    private final Environment env;

    public GraphQLLearnApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GraphQLLearnApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        try {
            Environment env = app.run(args).getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
