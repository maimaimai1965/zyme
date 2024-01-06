package ua.mai.graphql.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import ua.mai.graphql.config.GraphQlConfig;
import ua.mia.zyme.common.DefaultProfileUtil;
import ua.mia.zyme.common.SpringUtil;

@SpringBootApplication(
    scanBasePackages = {
        "ua.mai.graphql",
        "ua.mai.library.repository"
    }
)
@Import({
      GraphQlConfig.class,
})
public class GraphqlLearnApplication {

    private static final Logger log = LoggerFactory.getLogger(GraphqlLearnApplication.class);

    private final Environment env;

    public GraphqlLearnApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
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
