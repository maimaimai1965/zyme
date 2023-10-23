package ua.mai.zyme.graphql.zoo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ua.mai.zyme.graphql.authentication.GraphqlZooWebSecurityConfigurerAdapter;

/**
 * <pre>
 * Конфигурация для security.
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
      "ua.mai.zyme.graphql.zoo.controllers",
})
@Import({
        GraphqlZooWebSecurityConfigurerAdapter.class,
//        JwtAuthenticationFilter.class,
//        AuthenticationAuthorizationHandler.class
})
public class GraphZooSecurityConfig {
}
