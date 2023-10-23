package ua.mai.zyme.security.oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class ResourceServerZooConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .mvcMatcher("/zoo/**")
                .authorizeRequests()
//            .mvcMatchers("/zoo/**")
//                .access("hasAuthority('SCOPE_zoo.read')")
            .and()
            .oauth2ResourceServer()
                .jwt()
        ;
        return http.build();
    }
}