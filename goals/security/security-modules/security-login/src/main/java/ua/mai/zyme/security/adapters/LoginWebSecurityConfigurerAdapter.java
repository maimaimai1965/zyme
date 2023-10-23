package ua.mai.zyme.security.adapters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.mai.zyme.db.config.JdbcConfigurationDefaultSettings;
import ua.mai.zyme.security.authentication.AaJdbcUserDetailsManager;
import ua.mai.zyme.security.authentication.AuthenticationAuthorizationHandler;
import ua.mai.zyme.security.authentication.CustomPasswordEncoder;
import ua.mai.zyme.security.handlers.CustomAuthenticationFailureHandler;
import ua.mai.zyme.security.handlers.CustomAuthenticationSuccessHandler;

import javax.sql.DataSource;

@Component
//@Configuration
//@Import({
////        AuthenticationAuthorizationHandler.class,
//        CustomAuthenticationSuccessHandler.class,
//        CustomAuthenticationFailureHandler.class
//})
public class LoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

//  @Autowired
//  private AuthenticationAuthorizationHandler authenticationAuthorizationHandler;

  @Autowired
  private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

  @Autowired
  private CustomAuthenticationFailureHandler authenticationFailureHandler;

  @Bean
  public UserDetailsService userDetailsService(
               @Qualifier(JdbcConfigurationDefaultSettings.DATASOURCE_DEFAULT) DataSource dataSource) {
    return new AaJdbcUserDetailsManager(dataSource);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new CustomPasswordEncoder();
  }


  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
        .httpBasic()
        .and()
        .formLogin()
//            .defaultSuccessUrl("/home", true)
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
        .and()
        .authorizeRequests()
            .mvcMatchers("/no_auth")
                .permitAll()
            .anyRequest().authenticated();
  }


}
