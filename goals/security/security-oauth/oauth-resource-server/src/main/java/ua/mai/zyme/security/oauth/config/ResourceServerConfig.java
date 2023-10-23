package ua.mai.zyme.security.oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class ResourceServerConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//            .mvcMatcher("/articles/**")
//                .authorizeRequests()
            .authorizeRequests()
                .mvcMatchers("/articles/**") // Все запросы, c URL "/articles/**" не зависимо от используемого метода HTTP:
                    .authenticated()           //   доступны только аутентифицированным пользователям.
                .mvcMatchers("/zoo/**")      // Все запросы, c URL "/zoo/**" не зависимо от используемого метода HTTP:
                    .authenticated()
//                .access("hasAuthority('SCOPE_articles.read')")
            .and()
            .oauth2ResourceServer()
                .jwt();
        return http.build();
    }
}