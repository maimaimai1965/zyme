package ua.mai.zyme.mockserver.r2dbcmysql.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import ua.mai.zyme.r2dbcmysql.webclient.R2dbsMysqlWebClient;
import ua.mai.zyme.r2dbcmysql.webclient.log.LogService;
import ua.mai.zyme.r2dbcmysql.webclient.property.WebClientProperty;
import ua.mai.zyme.r2dbcmysql.webclient.property.ZymeR2dbcMysqlProperty;

@TestConfiguration
@EnableConfigurationProperties({
        ZymeR2dbcMysqlProperty.class
})
public class AppTestConfig {

    @Bean
    public ConnectionFactory connectionFactory(@Value("${spring.r2dbc.url}") String url,
                                               @Value("${spring.r2dbc.username}") String username,
                                               @Value("${spring.r2dbc.password}") String password) {
        return ConnectionFactoryBuilder
                .withUrl(url)        // URL тестовой БД
                .username(username)
                .password(password)
                .build();
    }

    @Bean
    ConnectionFactoryInitializer initializer(@Qualifier("connectionFactory")
                                             ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
//        ResourceDatabasePopulator resource =
//                new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
//        initializer.setDatabasePopulator(resource);
        return initializer;
    }


    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }

    @Bean
    public R2dbsMysqlWebClient r2dbsMysqlWebClient(ZymeR2dbcMysqlProperty zymeR2dbcMysqlProperty) {
        return new R2dbsMysqlWebClient(zymeR2dbcMysqlProperty.getWebclient());
    }


//    // Используется для тестирования контроллеров на запущенном приложении.
//    @Bean
//    public WebTestClient webTestClient() {
//        return WebTestClient.bindToServer()
//                .baseUrl("http://localhost:8080")
//                .build();
//    }

//    @Bean
//    public R2dbcTransactionManager transactionManager(@Qualifier("connectionFactory")
//                                                      ConnectionFactory connectionFactory) {
//        return new R2dbcTransactionManager(connectionFactory);
//    }

}