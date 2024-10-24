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
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.util.TestUtil;
import ua.mai.zyme.r2dbcmysql.webclient.R2dbsMysqlWebClient;
import ua.mai.zyme.r2dbcmysql.webclient.exception.AppClientError;
import ua.mai.zyme.r2dbcmysql.webclient.property.ZymeR2dbcMysqlProperty;

import java.util.ArrayList;
import java.util.List;

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
            System.out.println("\n-------- Приложение запущено! Выполняются действия: ------------");

            insertMember();
            insertMember_ERR101_NotNullNewMemberId();
            findMembersByNameLike();
        };
    }


    private void insertMember() {
        System.out.println("\n*** insertMember() ***");

        Member memberIn = TestUtil.newMember("mikeTest");

        try {
            Member memberOut =  mysqlWebClient.insertMember(Mono.just(memberIn)).block();
            System.out.println("-> " + memberOut);
        } catch (Exception ex) {
            System.out.println("-> Exception: " + ex.getMessage());
//            ex.printStackTrace();
        }
    }

    private void insertMember_ERR101_NotNullNewMemberId() {
        System.out.println("\n*** insertMember_ERR101_NotNullNewMemberId() ***");

        Member memberIn = TestUtil.newMember("mikeTest");
        memberIn.setMemberId(-1);

        try {
            Member memberOut = mysqlWebClient.insertMember(Mono.just(memberIn)).block();
            System.out.println("-> " + memberOut);
        } catch (Exception ex) {
            System.out.println("-> Exception: " + ex.getMessage());
//            ex.printStackTrace();
        }

    }

    private void findMembersByNameLike() {
        System.out.println("\n*** findMembersByNameLike() ***");
        try {
//            mysqlWebClient.insertMember(Mono.just(TestUtil.newMember("vinsenTest"))).block();
//            mysqlWebClient.insertMember(Mono.just(TestUtil.newMember("bearnTest"))).block();
//            mysqlWebClient.insertMember(Mono.just(TestUtil.newMember("learnTest"))).block();
//            mysqlWebClient.insertMember(Mono.just(TestUtil.newMember("tomTest"))).block();

            System.out.println("# findMembersByNameLike():");
            List<Member> members = mysqlWebClient.findMembersByNameLike("nTest").toStream().toList();
            System.out.println("-> " + members);
        } catch (Exception ex) {
            System.out.println("-> Exception: " + ex.getMessage());
//            ex.printStackTrace();
        }
    }

}

