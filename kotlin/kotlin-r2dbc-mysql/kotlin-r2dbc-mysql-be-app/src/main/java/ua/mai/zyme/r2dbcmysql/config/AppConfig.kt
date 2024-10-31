package ua.mai.zyme.r2dbcmysql.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.r2dbc.connection.R2dbcTransactionManager

@Configuration
open class AppConfig {

    @Bean
    open fun initializer(@Qualifier("connectionFactory") connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)
//        val resource = ResourceDatabasePopulator(ClassPathResource("schema.sql"))
//        initializer.setDatabasePopulator(resource)
        return initializer
    }

//    @Bean
//    open fun transactionManager(@Qualifier("connectionFactory") connectionFactory: ConnectionFactory): R2dbcTransactionManager {
//        return R2dbcTransactionManager(connectionFactory)
//    }

}