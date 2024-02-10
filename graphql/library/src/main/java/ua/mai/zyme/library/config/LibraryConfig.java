package ua.mai.zyme.library.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * Конфигурация для этого модуля <i>library</i>.
 * Если модуль включается в приложение, то в аннотации <i>@SpringBootApplication</i> в <i>scanBasePackageClasses</i>
 * должен быть указан этот класс.
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
        "ua.mai.zyme.library.repository",
        "ua.mai.zyme.library.service"
})
public class LibraryConfig {
}
