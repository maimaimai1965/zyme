package ua.mai.zyme.security.oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
public class ClientSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//          .authorizeRequests(authorizeRequests ->
//            authorizeRequests.anyRequest().authenticated()
//          )
            .authorizeRequests()                  //
//              .mvcMatchers("/favicon.ico")      // ? Все запросы, c URL "/favicon.ico" не зависимо от используемого метода HTTP (а так же "/favicon.ico/"):
//                  .permitAll()                  //   не проверяются.

                .antMatchers("/zoo/**")         // ? Все запросы, c URL "/zoo/**" не зависимо от используемого метода HTTP:
                    .permitAll()                  //   не проверяются.
                .anyRequest()                     // Все запросы, независимо от URL или используемого метода HTTP:
                    .authenticated()              //   доступны только аутентифицированным пользователям.
          .and()
          .oauth2Login(oauth2Login ->
              oauth2Login.loginPage("/oauth2/authorization/articles-client-oidc")
                          )
          .oauth2Client(withDefaults());
        return http.build();
    }

}