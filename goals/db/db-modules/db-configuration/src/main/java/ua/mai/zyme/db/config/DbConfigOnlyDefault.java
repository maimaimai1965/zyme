package ua.mai.zyme.db.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * <pre>
 * Конфигурация для работы с БД схемой <i>zyme</i>.
 * Если модуль включается в приложение и необходим доступ к источнику данных к схеме <i>zyme</i>, то:
 *   - в аннотации <i>@Import</i> должен быть указан этот класс;
 *   - в свойствах приложения (в файле application.yml) должны быть заданы свойства:
 *
 * spring:
 *     main.banner-mode: off
 *     main:
 *         allow-bean-definition-overriding: true
 *     lifecycle.timeout-per-shutdown-phase: 9s
 *     application:
 *         name: Zyme REST Lib
 *     messages:
 *         basename: i18n/messages
 *     datasource:
 *         hikari:
 *             pool-name: "DefaultDataSource"
 *             auto-commit: true
 *             maximum-pool-size: 50
 *             idle-timeout: 30000
 *             connection-timeout: 1000
 *             max-lifetime: 2000000
 *             validation-timeout: 1000
 *             minimum-idle: 5
 *             driver-class-name: "com.mysql.cj.jdbc.Driver"
 *             jdbc-url: "jdbc:mysql://localhost:3309/zyme?autoReconnect=true&useSSL=false&serverTimezone=Europe/Kiev&maxReconnects=3&connectTimeout=10000"
 *             username: "root"
 *             password: "root"
 *     jooq:
 *         sqlDialect: MYSQL
 * zyme:
 *     db:
 *         schema: zyme
 *
 * Определяется конфигурация через дефолтные спринговые настройки - <i>JdbcConfigurationDefaultSettings</i>} и
 * <i>JdbcConfigurationDefault</i>).
 * </pre>
 */
@Configuration
@Import({ JdbcConfigurationDefault.class })
public class DbConfigOnlyDefault {
}
