package ua.mai.zyme.vaadin8.boot.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "ua.mai.zyme.vaadin8.ui",
})
public class Vaadin8UiConfiguration {
}
