package ua.mai.zyme.security.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import ua.mia.zyme.common.DefaultProfileUtil;
import ua.mia.zyme.common.SpringUtil;

@SpringBootApplication
public class OAuth2AuthorizationServerApplication {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthorizationServerApplication.class);

    public static void main(String[] args) {
        System.getProperties().setProperty("spring.application.name", "Security OAuth Authorization Server Application");
        SpringApplication app = new SpringApplication(OAuth2AuthorizationServerApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        try {
            Environment env = app.run(args).getEnvironment();
            SpringUtil.logApplicationStartup(log, env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}