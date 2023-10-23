package ua.mai.zyme.security.adapters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.stereotype.Component;
import ua.mai.zyme.db.config.JdbcConfigurationDefaultSettings;
import ua.mai.zyme.security.authentication.AaJdbcUserDetailsManager;
import ua.mai.zyme.security.authentication.CustomPasswordEncoder;
import ua.mai.zyme.security.filters.CsrfTokenLoggerFilter;
import ua.mai.zyme.security.handlers.CustomAuthenticationFailureHandler;
import ua.mai.zyme.security.handlers.CustomAuthenticationSuccessHandler;

import javax.sql.DataSource;

/**
 * <pre>
 *
 * TODO:
 * 1. Все запросы с URL <i>"/product/addWithoutCheckCsr"</i> не проходят аутентификацию пользователя и проверку CSRF.
 * 2. POST запросы с URL "/login" Для них проводится
 *    проверка на
 *    <b>статический ключ</b> определенный в <i>application.yml</i> свойством <i>authorization.key</i>. Статический ключ
 *    должен передаваться в HEAD-ре запроса с названием <i>Authorization</i>. Проверка осуществляется в фильтре
 *    <i>StaticKeyAuthenticationFilter</i>. Этот фильтр вставляется в цепочку фильтров после фильтра
 *    <i>BasicAuthenticationFilter</i>. Чтобы фильтр <i>BasicAuthenticationFilter</i> не обрабатывал этот URL для него
 *    добавляется разрешение для всех запросов - <i>mvcMatchers("/hiWithStaticKey").permitAll()</i>.
 * 2. Остальные запросы проходят <b>basic</b> аутентификацию с проверкой пользователя и пароля.
 * </pre>
 */
@Component
//@Configuration
//@Import({
////        AuthenticationAuthorizationHandler.class,
//        CustomAuthenticationSuccessHandler.class,
//        CustomAuthenticationFailureHandler.class
//})
public class CsrfWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {


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
       .csrf(c -> {
//              c.csrfTokenRepository(customTokenRepository());
              c.ignoringAntMatchers("/product/addWithoutCheckCsr");

//            HandlerMappingIntrospector i = new HandlerMappingIntrospector();
//            MvcRequestMatcher r = new MvcRequestMatcher(i, "/product/addWithoutCheckCsr");
//            c.ignoringRequestMatchers(r);

//            String pattern = ".*[0-9].*";
//            String httpMethod = HttpMethod.POST.name();
//            RegexRequestMatcher r = new RegexRequestMatcher(pattern, httpMethod);
//            c.ignoringRequestMatchers(r);
            })
        .httpBasic()
        .and()
        .addFilterAfter(                        // staticKeyAuthenticationFilter is added to the filters chain after BasicAuthenticationFilter.
              new CsrfTokenLoggerFilter(),      //
              CsrfFilter.class)  //
        .formLogin()
//            .defaultSuccessUrl("/home", true)
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
        .and()
        .authorizeRequests()
//            .mvcMatchers("")
//                .permitAll()
            .mvcMatchers("/no_auth")
                .permitAll()
            .mvcMatchers("/product/addWithoutCheckCsr")
                .permitAll()
            .anyRequest().authenticated();
  }


}
