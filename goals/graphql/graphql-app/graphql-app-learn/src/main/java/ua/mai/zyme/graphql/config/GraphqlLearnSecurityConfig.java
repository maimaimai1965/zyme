package ua.mai.zyme.graphql.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ua.mai.zyme.graphql.authentication.GraphqlLearnWebSecurityConfigurerAdapter;

/**
 * <pre>
 * Конфигурация для security.
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
//      "ua.mai.zyme.graphql.controllers",
})
@Import({
        GraphqlLearnWebSecurityConfigurerAdapter.class,
//        JwtAuthenticationFilter.class,
//        AuthenticationAuthorizationHandler.class
})
public class GraphqlLearnSecurityConfig {
}
