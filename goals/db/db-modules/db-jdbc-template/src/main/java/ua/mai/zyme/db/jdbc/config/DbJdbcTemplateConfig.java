package ua.mai.zyme.db.jdbc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * Конфигурация для этого модуля <i>db-jdbc-template</i>.
 * Если модуль включается в приложение, то в аннотации <i>@SpringBootApplication</i> в <i>scanBasePackageClasses</i>
 * должен быть указан этот класс.
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
      "ua.mai.zyme.db.jdbc"
})
public class DbJdbcTemplateConfig {
}
