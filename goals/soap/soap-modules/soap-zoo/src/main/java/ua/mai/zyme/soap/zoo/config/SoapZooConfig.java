package ua.mai.zyme.soap.zoo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * Конфигурация для этого модуля <i>soap-zoo</i>.
 * Если модуль включается в приложение, то в аннотации <i>@SpringBootApplication</i> в <i>scanBasePackageClasses</i>
 * должен быть указан этот класс.
 * </pre>
 */
@Configuration
@ComponentScan(basePackages = {
        "ua.mai.zyme.soap.zoo.services"
})
public class SoapZooConfig {
}
