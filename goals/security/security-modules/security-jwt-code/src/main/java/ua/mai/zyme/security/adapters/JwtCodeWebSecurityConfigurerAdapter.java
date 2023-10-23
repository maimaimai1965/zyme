package ua.mai.zyme.security.adapters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ua.mai.zyme.db.config.JdbcConfigurationDefaultSettings;
import ua.mai.zyme.security.authentication.AuthenticationAuthorizationHandler;
import ua.mai.zyme.security.authentication.H2JdbcUserDetailsManager;
import ua.mai.zyme.security.controllers.AuthController;
import ua.mai.zyme.security.controllers.HelloRestController;
import ua.mai.zyme.security.filters.JwtAuthenticationFilter;

import javax.annotation.security.RolesAllowed;
import javax.sql.DataSource;

/**
 * <pre>
 * Security конфигурация для приложения <i><b>SecurityJwtBasicApplication</i></b>, реализующая алгоритм:
 *
 * a) Получение кода для пользователя по <b>URL <i>"/login"</i></b> с использованием <i>Basic</i> токена.
 *    Выполняется запрос с <b>URL <i>"/login"</i></b>, содержащий Header с названием <i>"Authorization"</i> и <i>Basic</i> токеном:
 *          <i>curl --location 'http://localhost:8115/login' --header 'Authorization: Basic YW5uOjEy'</i>
 *    1. Запрос с этим URL проходит Basic аутентификацию в {@link BasicAuthenticationFilter}.
 *       Для этого URL задается необходимость прохождения Basic аутентификации - <i>mvcMatchers("/login").authenticated()</i>.
 *       Данные о пользователе и его ролях сохраняются в объекте {@link Authentication} в {@link SecurityContext}.
 *    2. Если аутентификация пройдена, то далее обрабатывается фильтр {@link JwtAuthenticationFilter}, который не срабатывает
 *       на этом URL (см. метод <i>shouldNotFilter()</i>).
 *    3. Далее срабатывает метод контроллера {@link AuthController}.<i>login()</i>, в котором проверяется отсутствие
 *       Header-а с именем <i>"code"</i>. Если такового нет, то генерится код, который сохраняется в БД и в
 *       <i>response</i> добавляется Header с этим кодом с именем <i>"code"</i> .
 *
 * b) Получение <i>JWT</i> токена для пользователя по <b>URL <i>"/login"</i></b> с использованием <i>Basic</i> токена и
 *    сгенерированного кода.
 *    Выполняется запрос с <b>URL <i>"/login"</i></b>, содержащий два Header-а: первый с именем <i>"Authorization"</i> и
 *    <i>Basic</i> токеном, а второй - с именем <i>"code"</i> и с кодом:
 *          <i>curl --location 'http://localhost:8115/login' --header 'code: 1948' --header 'Authorization: Basic YW5uOjEy'</i>
 *    1. Запрос с этим URL проходит Basic аутентификацию в {@link BasicAuthenticationFilter}.
 *       Для этого URL задается необходимость прохождения Basic аутентификации - <i>mvcMatchers("/login").authenticated()</i>.
 *       Данные о пользователе и его ролях сохраняются в объекте {@link Authentication} в {@link SecurityContext}.
 *    2. Если аутентификация пройдена, то далее обрабатывается фильтр {@link JwtAuthenticationFilter}, который не срабатывает
 *       на этом URL (см. метод <i>shouldNotFilter()</i>).
 *    3. Далее срабатывает метод контроллера {@link AuthController}.<i>login()</i>, в котором проверяется наличие
 *       Header-а с именем <i>"code"</i>. Если таковой есть, то код проверяется с кодом считанным из БД. Если коды
 *       совпадают, то генерится JWT токен включающий имя пользователя и его роли, которые забираются из объекта
 *       {@link Authentication} из {@link SecurityContext}.
 *       Этот JWT токен возвращается в <i>response</i> в Header-е с названием <i>"Authorization"</i>.
 *
 * с) Запрос с использованием полученного JWT токена:
 *    Выполняется запрос с <b>URL отличным от <i>"/login"</i></b>, содержащий Header с названием <i>"Authorization"</i> и <i>JWT</i> токеном:
 *          <i>curl --location 'http://localhost:8115/hello' --header 'Authorization: eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6IlJPTEVfUkVBRCIsInVzZXJuYW1lIjoiYW5uIn0.T0V4KfcUt8HefeGNm3Vv4dnyMAwx5SkRNE51kE41pWg'</i>
 *    1. {@link BasicAuthenticationFilter} не проводит аутентификацию, т.к. это не URL <i>"/login"</i>.
 *       Для остальных URL аутентификация не нужна - <i>anyRequest().permitAll()</i>.
 *    2. В {@link JwtAuthenticationFilter} в методе <i>doFilterInternal()</i> проверяется наличие Header-а с названием
 *       <i>"Authorization"</i> и <i>JWT</i> токеном.
 *       Если он есть и валидный, то из него извлекаются имя пользователя и его роли, которые используются при создании
 *       объекта {@link Authentication}. Этот объект сохраняется в {@link SecurityContext}.
 *    3. Далее вызывается метод контроллера {@link HelloRestController}, соответствующего URL.
 *       Доступ к методу авторизируется для ролей, которые заданы для него аннотацией {@link RolesAllowed}.
 *       Чтобы эти аннотации срабатывали необходимо для нашей конфигурации включить их обработку -
 *       </i>@EnableGlobalMethodSecurity(jsr250Enabled = true)</i>.
 *
 * d) Запрос без использования JWT токена:
 *          <i>curl --location 'http://localhost:8115/hello'</i>
 *    Выполняется запрос с <b>URL отличным от <i>"/login"</i></b>, без Header-а с названием <i>"Authorization"</i>.
 *    1. {@link BasicAuthenticationFilter} не проводит аутентификацию, т.к. это не URL <i>"/login"</i>.
 *       Для остальных URL аутентификация не нужна - <i>anyRequest().permitAll()</i>.
 *    2. В {@link JwtAuthenticationFilter} в методе <i>doFilterInternal()</i> проверяется наличие Header-а с названием
 *       <i>"Authorization"</i>.
 *       Если его, нет то возвращается <i>response</i> со статусом <i>401 Unauthorized</i>.
 *
 * Дополнительно в конфигурации:
 * 1. Отключается CSRF фильтр - <i>csrf().disable()</i>.
 * 2. Не используется X-Frame-Options - <i>headers().frameOptions().disable()</i>.  Нужно для консоли H2.
 * 3. Отключается @link FormLogin фильтр - <i>formLogin().disable()</i>.
 * 4. Предотвращается сохранения запроса в сессии - <i>sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)</i>.
 *
 * Объявляются бины:
 * 1. <i>userDetailsService</i>: {@link H2JdbcUserDetailsManager}, который использует H2 схему zyme03.
 *    Используются таблицы <i>USER, AUTHORITY</i>.
 *    Manager предоставляет авторизацию по ролям без использованием групп ролей.
 * 2. <i>passwordEncoder</i>: {@link NoOpPasswordEncoder} - пароли при обработке не кодируются;.
 *
 * </pre>
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(
//        prePostEnabled = true,  // Enables @PreAuthorize and @PostAuthorize
//        securedEnabled = true,  // Enables @Secured
        jsr250Enabled = true    // Enables @RolesAllowed (Ensures JSR-250 annotations are enabled)
)
@Configuration
public class JwtCodeWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

  @Autowired
  private AuthenticationAuthorizationHandler authenticationAuthorizationHandler;

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;


  @Bean
  public UserDetailsService userDetailsService(
               @Qualifier(JdbcConfigurationDefaultSettings.DATASOURCE_DEFAULT) DataSource dataSource) {
    return new H2JdbcUserDetailsManager(dataSource);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
//    return new CustomPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
        .csrf().disable()                       // Не используется CSRF фильтр () (нужен для консоли H2).
        .headers().frameOptions().disable()     // Не используется X-Frame-Options - H2 database console runs inside a frame (нужен для консоли H2).
        .and()                                  //
        .formLogin().disable()                  // Не используется FormLogin фильтр.
        .sessionManagement()                    //
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Preventing the request from being saved in the session.
        .and()                                  //
        .httpBasic()                            // Instance of the BasicAuthenticationFilter is added to the filters chain.
        .and()                                  //
        .authorizeRequests()                    //
            .mvcMatchers("/login")              // Все запросы, c URL "/login" не зависимо от используемого метода HTTP (а так же "/login/"):
                .authenticated()                //   доступны только аутентифицированным пользователям.
            .anyRequest()                       // Все запросы, независимо от URL или используемого метода HTTP:
                .permitAll()                    //   не проверяются BasicAuthenticationFilter.
        .and()                                  //
        .addFilterAfter(                        // jwtAuthenticationFilter is added to the filters chain after BasicAuthenticationFilter.
              jwtAuthenticationFilter,          // jwtAuthenticationFilter проверяет JWT Authorisation token.
              BasicAuthenticationFilter.class)  //
    ;
  }

}
