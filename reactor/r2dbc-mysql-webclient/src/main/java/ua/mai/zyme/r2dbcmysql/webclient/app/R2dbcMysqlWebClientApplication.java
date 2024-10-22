package ua.mai.zyme.r2dbcmysql.webclient.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.util.TestUtil;
import ua.mai.zyme.r2dbcmysql.webclient.R2dbsMysqlWebClient;
import ua.mai.zyme.r2dbcmysql.webclient.property.ZymeR2dbcMysqlProperty;

@SpringBootApplication(exclude = {
        R2dbcAutoConfiguration.class
})
@EnableConfigurationProperties({
        ZymeR2dbcMysqlProperty.class
})
public class R2dbcMysqlWebClientApplication {

    @Autowired
    private ZymeR2dbcMysqlProperty zymeR2dbcMysqlProperty;

    private R2dbsMysqlWebClient mysqlWebClient;


    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(R2dbcMysqlWebClientApplication.class);
        // Указываем, что это не веб-приложение (не запускаем веб-сервер)
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);

    }

    @Bean
    public CommandLineRunner run() {
        mysqlWebClient = new R2dbsMysqlWebClient(zymeR2dbcMysqlProperty.getWebclient());

        return args -> {
            // Выполняем действие при старте приложения
            System.out.println("*** Приложение запущено! Выполняются действия: ***");

            f();
        };
    }

    private void f() {
        Member memberIn = TestUtil.newMember("mikeTest");

        Member memberOut =  mysqlWebClient.insertMember(Mono.just(memberIn)).block();
        System.out.println("memberOut =" + memberOut);

    }


}

