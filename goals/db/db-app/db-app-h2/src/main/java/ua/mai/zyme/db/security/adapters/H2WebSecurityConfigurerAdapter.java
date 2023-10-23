package ua.mai.zyme.db.security.adapters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Используется для обеспечения доступа к консоле H2.
 */
@Configuration
public class H2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
//        .httpBasic()                            // Instance of the BasicAuthenticationFilter is added to the filters chain.
//        .and()                                  //
//        .authorizeRequests()                    //
//            .mvcMatchers("/login")              // Все запросы, c URL "/login" не зависимо от используемого метода HTTP (а так же "/login/"):
//                .authenticated()                //   доступны только аутентифицированным пользователям.
//            .anyRequest()                       // Все запросы, независимо от URL или используемого метода HTTP:
//                .permitAll()                    //   не проверяются BasicAuthenticationFilter.
//        .and()                                  //
        .csrf().disable()                       // Не используется CSRF фильтр (нужен для консоли H2).
        .headers().frameOptions().disable()     // Не используется X-Frame-Options - H2 database console runs inside a frame
    ;
  }


}
