package ua.mai.zyme.r2dbcmysql.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import reactor.core.publisher.Mono;


@Configuration
public class AuditorAwareConfiguration {

  @Bean
  ReactiveAuditorAware<String> auditorAware() {
    return () -> Mono.just("AUDITOR");
  }

}
