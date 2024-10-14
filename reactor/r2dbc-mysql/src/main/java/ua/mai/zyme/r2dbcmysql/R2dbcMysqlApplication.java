package ua.mai.zyme.r2dbcmysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import ua.mai.zyme.r2dbcmysql.util.DefaultProfileUtil;
import ua.mai.zyme.r2dbcmysql.util.SpringUtil;

@SpringBootApplication
//@EnableR2dbcAuditing
public class R2dbcMysqlApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(R2dbcMysqlApplication.class, args);
//
//    }

    private static final Logger log = LoggerFactory.getLogger(R2dbcMysqlApplication.class);

    private final Environment env;

    public R2dbcMysqlApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
//        System.getProperties().setProperty("spring.application.name", "r2dbc-mysql REST service");
//        SpringApplication.run(R2dbcMysqlApplication.class, args);

        SpringApplication app = new SpringApplication(R2dbcMysqlApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        try {
            Environment env = app.run(args).getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
