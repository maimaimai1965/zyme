package ua.mai.zyme.security.adapters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ua.mai.zyme.db.config.JdbcConfigurationDefaultSettings;
import ua.mai.zyme.security.authentication.AaJdbcUserDetailsManager;
import ua.mai.zyme.security.authentication.AuthenticationAuthorizationHandler;
import ua.mai.zyme.security.authentication.CustomPasswordEncoder;
import ua.mai.zyme.security.filters.AuthenticationLoggingFilter;
import ua.mai.zyme.security.filters.BeforeAuthenticationFilter;
import ua.mai.zyme.security.filters.StaticKeyAuthenticationFilter;

import javax.sql.DataSource;

/**
 * <pre>
 * Security конфигурация для приложения <i><b>SecurityBasicOrKeyApplication</i></b>, реализующая алгоритм:
 *
 * 1. Если в Header-е запроса есть параметр <b><i>"Not-Process"</i></b>, то такой запрос <b>не обрабатывается</b>.
 *    Эта проверка осуществляется в фильтре {@link BeforeAuthenticationFilter}, который вставляется в цепочку фильтров
 *    перед фильтром {@link BasicAuthenticationFilter}.
 * 2. Все запросы с URL <i>"/hiWithStaticKey"</i> проходят аутентификацию на совпадение со статическим ключом.
 *    В запросе ключ должен быть задан в Header-е с названием <i>Authorization</i>.
 *    Проверка ключа осуществляется в фильтре {@link StaticKeyAuthenticationFilter}. Этот фильтр вставляется в цепочку
 *    фильтров после фильтра {@link BasicAuthenticationFilter}.
 *    Для запроса проводится проверка на <b>статический ключ</b>, определенный в <i>application.yml</i> свойством <i>authorization.key</i>.
 *    Чтобы фильтр {@link BasicAuthenticationFilter} не обрабатывал этот URL, для него добавляется разрешение для всех
 *    запросов с URL <i>"/hiWithStaticKey"</i> - <i>mvcMatchers("/hiWithStaticKey").permitAll()</i>.
 * 3. Остальные запросы проходят <b>Basic</b> аутентификацию с проверкой пользователя и пароля.
 *    Чтобы фильтр {@link BasicAuthenticationFilter} аутентифицировал эти URL, для него добавляется условие
 *    <i>anyRequest().authenticated()</i>.
 *    Проверка проведения Basic аутентификации проводится в {@link StaticKeyAuthenticationFilter}.
 *
 * Дополнительно в конфигурации:
 * 1. Отключается FormLogin фильтр - <i>formLogin().disable()</i>.
 * 2. Отключается CSRF фильтр.
 * 3. Предотвращается сохранения запроса в сессии - <i>sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)</i>.
 * 4. Добавляется фильтр {@link AuthenticationLoggingFilter} после {@link StaticKeyAuthenticationFilter}, который может
 *    осуществлять логирование.
 *
 * Объявляются бины:
 * 1. <i>userDetailsService</i>: {@link AaJdbcUserDetailsManager}, который использует MySQL схему zyme.
 *    Используются таблицы <i>AA_USER, AA_ROLE, AA_USER_ROLE, AA_GROUP, AA_GROUP_ROLE, AA_GROUP_MEMBER</i>.
 *    Manager предоставляет авторизацию по ролям с использованием групп ролей.
 * 2. <i>passwordEncoder</i>: {@link CustomPasswordEncoder}.
 * </pre>
 */
@Configuration
public class BasicOrKeyWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

  @Autowired
  private AuthenticationAuthorizationHandler authenticationAuthorizationHandler;

  @Bean
  public UserDetailsService userDetailsService(
               @Qualifier(JdbcConfigurationDefaultSettings.DATASOURCE_DEFAULT) DataSource dataSource) {
    return new AaJdbcUserDetailsManager(dataSource);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new CustomPasswordEncoder();
  }


  @Autowired
  private StaticKeyAuthenticationFilter staticKeyAuthenticationFilter;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
        .formLogin().disable()                  // Не используется FormLogin фильтр.
        .csrf().disable()                       // Не используется CSRF фильтр, чтобы разрешить вызов c помощью метода HTTP POST, PUT
        .sessionManagement()                    //
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Preventing the request from being saved in the session.
        .and()                                  //
        .addFilterBefore(                       // Instance of the BeforeAuthenticationFilter is added to the filters chain before BasicAuthenticationFilter.
              new BeforeAuthenticationFilter(), //
              BasicAuthenticationFilter.class)  //
        .httpBasic()                            // Instance of the BasicAuthenticationFilter is added to the filters chain.
        .and()                                  //
        .authorizeRequests()                    //
            .mvcMatchers("/hiWithStaticKey")  // Все запросы, c URL "/hiWithStaticKey" не зависимо от используемого метода HTTP (а так же "/hiWithStaticKey/"):
                .permitAll()                    //   не проверяются BasicAuthenticationFilter.
            .anyRequest()                       // Все запросы, независимо от URL или используемого метода HTTP:
                .authenticated()                //   доступны только аутентифицированным пользователям.
        .and()                                  //
        .addFilterAfter(                        // staticKeyAuthenticationFilter is added to the filters chain after BasicAuthenticationFilter.
              staticKeyAuthenticationFilter,    //
              BasicAuthenticationFilter.class)  //
        .addFilterAfter(                        // Instance of the AuthenticationLoggingFilter is added to the filters chain after staticKeyAuthenticationFilter.
              new AuthenticationLoggingFilter(),//
              staticKeyAuthenticationFilter.getClass())
       ;

//    http.httpBasic()                            // Instance of the BasicAuthenticationFilter is added to the filters chain.
//        .and()
//        .authorizeRequests()                    //
//            .mvcMatchers("/hello")              // Все запросы, c URL "/hello" не зависимо от используемого метода HTTP (а так же "/hello/"):
//                .hasRole("ADMIN")               //  доступны для пользователей с ролью ROLE_ADMIN.
//            .mvcMatchers("/ciao")               // Все запросы, c URL "/ciao" не зависимо от используемого метода HTTP (а так же "/ciao/"):
//                .hasAnyRole("ADMIN", "MANAGER") //   доступны для пользователей с ролью ROLE_ADMIN или ROLE_MANAGER.
//            .mvcMatchers(HttpMethod.GET, "/a")  // Все GET запросы, c URL "/a" (а так же "/a/"):
//                .authenticated()                //   доступны только аутентифицированным пользователям.
//            .mvcMatchers(HttpMethod.POST, "/a") // Все POST запросы, c URL "/a" (а так же "/a/"):
//                .permitAll()                    //   доступны всем пользователям.
//            .mvcMatchers("/a/b/**")             // Все запросы, c URL начинающегося с "/a/b/" не зависимо от используемого метода HTTP:
//                .authenticated()                //   доступны только аутентифицированным пользователям.
//            .mvcMatchers("/a/**/c")                    // Все запросы, c URL начинающегося с "/a/" и заканчивающиеся "/c" не зависимо от используемого метода HTTP:
//                .hasAnyAuthority("ROLE_WR", "ROLE_RD") //   доступны для пользователей с ролью ROLE_WR или ROLE_RD.
//            .mvcMatchers("/product/{code:^[0-9]*$}")   // Все запросы, c URL начинающегося с "/product/" и далее содержащего строку из
//                                                       // цифр (PathVariable с именем code в методе REST контролера
//                                                       //         @GetMapping("/product/{code}")
//                                                       //         public String productCode(@PathVariable String code) {...}) :
//                .permitAll()                           // доступны всем пользователям.
//            .anyRequest()                       // Все остальные запросы:
//                .permitAll()                    //   доступны всем пользователям.
//    //            .denyAll();                     //   не доступны никому.
//    //            .authenticated()                //   доступны только аутентифицированным пользователям.
//        .and()
//        .formLogin().disable()                  // Не используется FormLogin фильтр.
//        .csrf().disable()                       // Не используется CSRF фильтр, чтобы разрешить вызов c помощью метода HTTP POST, PUT
//    ;


//    http.httpBasic()
//        .and()
//        .authorizeRequests()
//            .anyRequest()      // Все запросы, независимо от URL или используемого метода HTTP
//            .permitAll();  //   доступны любым пользователям (в том числе и не аутентифицированным)
//    http.httpBasic()
//        .and()
//        .authorizeRequests()
//            .anyRequest()     // Все запросы, независимо от URL или используемого метода HTTP
//                .denyAll();   //   запрещены для всех (имеют ограниченный доступ).
//    http.httpBasic()
//        .and()
//        .authorizeRequests()
//            .anyRequest()                            // Все запросы, независимо от URL или используемого метода HTTP
//                .hasAnyAuthority("WRITE", "READ")    //   доступны для пользователей с ролью WRITE или READ.
//    //            .hasAuthority("WRITE")               //   доступны для пользователей с ролью WRITE.
//    //            .access("hasAuthority('WRITE')")     //   доступны для пользователей с ролью WRITE (Not recommend because parameter - Spring expression (SpEL)!)
//    ;

//    http.httpBasic()
//        .and()
//        .authorizeRequests()
//            .anyRequest()                                                        // Все запросы, независимо от URL или используемого метода HTTP
//                                                                                 // доступны в соответствии со Spring expression (SpEL):
//                .access("hasAuthority('read') and !hasAuthority('delete')");                        // parameter - Spring expression (SpEL)
//    //            .access("T(java.time.LocalTime).now().isAfter(T(java.time.LocalTime).of(12, 0))") // parameter - Spring expression (SpEL)
//    ;

//    http.httpBasic()
//        .and()
//        .authorizeRequests()
//            .anyRequest()                        // Все запросы, независимо от URL или используемого метода HTTP
//                .hasAnyRole("ADMIN", "USER")     // доступны для пользователей с ролью ROLE_WRITE или ROLE_READ.
//    //            .hasRole("ADMIN")              // доступны для пользователей с ролью ROLE_WRITE.
//    ;





//    http
//        .csrf().disable()
//        .authorizeRequests().anyRequest()
//        .httpBasic()
//            .authenticationEntryPoint(authenticationAuthorizationHandler)
////        .requestMatchers().antMatchers("/hello/**")
//        .and()
//        .formLogin()
//            .disable()
//        ;

//    http.requestMatchers().antMatchers("/hello/**")
//            .and()
//               .httpBasic()
//               .authenticationEntryPoint(authenticationAuthorizationHandler)
//            .and()
//            .csrf().disable()
//            .sessionManagement()
//            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            .and()
//            .authenticationManager(authenticationManager)
//            .authorizeRequests()
//            .antMatchers(servicesProperties.getPartnerQuotaRefill().getUrl()).hasAnyAuthority(
//                    Util.toStringArray(servicesProperties.getPartnerQuotaRefill().getRoles()))
//            .antMatchers(servicesProperties.getTransactionHistory().getUrl()).hasAnyAuthority(
//                    Util.toStringArray(servicesProperties.getTransactionHistory().getRoles()))
//            .antMatchers(servicesProperties.getTopUpNotification().getUrl()).hasAnyAuthority(
//                    Util.toStringArray(servicesProperties.getTopUpNotification().getRoles()))
//            .antMatchers(servicesProperties.getApiUrl()).hasAnyAuthority(
//                    Util.toStringArray(servicesProperties.getPartnerQuotaRefill().getRoles() + "," +
//                            servicesProperties.getTransactionHistory().getRoles() + "," +
//                            servicesProperties.getTopUpNotification().getRoles())
//            )
//            .and().exceptionHandling()
//            .accessDeniedHandler(authenticationAuthorizationHandler)
//            .and()
//            .formLogin()
//              .disable();
  }


}
