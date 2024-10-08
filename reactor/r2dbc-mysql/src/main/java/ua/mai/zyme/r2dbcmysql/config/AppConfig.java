package ua.mai.zyme.r2dbcmysql.config;


import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;

@Configuration
public class AppConfig {

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


//    @Bean
//    public R2dbcTransactionManager transactionManager(@Qualifier("connectionFactory")
//                                                      ConnectionFactory connectionFactory) {
//        return new R2dbcTransactionManager(connectionFactory);
//    }

}