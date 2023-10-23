package ua.mai.zyme.db.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * <pre>
 * Конфигурация для этого модуля <i>db-configuration</i>.
 * Если модуль включается в приложение и необходим доступ к двум источникам данных (к схеме <i>zyme</i> и к схеме
 * <i>zyme02</i>), то в аннотации <i>@SpringBootApplication</i> в свойстве <i>scanBasePackageClasses</i> должен быть
 * указан этот класс.
 *
 * В модуле определяется конфигурация для для двух источников данных (DataSource):
 *   - к БД MySQL к схеме <i>zyme</i>. Конфигурация определяется через дефолтные спринговые настройки (
 *       <i>JdbcConfigurationDefaultSettings</i>},  <i>JdbcConfigurationDefault</i>).
 *   - к БД MySQL к схеме <i>zyme02</i>. Конфигурация определяется через свои настройки (
 *        <i>JdbcConfigurationZyme02Settings</i>, <i>JdbcConfigurationZyme02</i>).
 * </pre>
 */
@Configuration
@Import({
        JdbcConfigurationDefault.class,
        JdbcConfigurationZyme02.class,
})
//@ComponentScan(basePackages = {
//      "ua.mai.zyme.db.config"
//})
public class DbConfig {
}
