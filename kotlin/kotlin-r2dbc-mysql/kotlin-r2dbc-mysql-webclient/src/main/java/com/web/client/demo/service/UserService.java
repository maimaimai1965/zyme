package com.web.client.demo.service;

import com.web.client.demo.model.User;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private static final int MAX_RETRY_ATTEMPTS = 3;
  private static final int DELAY_MILLIS = 100;
  private final WebClient webClient;
  static final String USERS_URL_TEMPLATE = "/users/{id}";
  static final String BROKEN_URL_TEMPLATE = "/broken-url/{id}";

  public Mono<User> getUserByIdAsync(final String id) {
    return webClient.get()
        .uri(USERS_URL_TEMPLATE, id)
        .retrieve()
        .bodyToMono(User.class);
  }

  public User getUserByIdSync(final String id) {
    return webClient.get()
        .uri(USERS_URL_TEMPLATE, id)
        .retrieve()
        .bodyToMono(User.class)
        .block();
  }

  public User getUserWithRetrySync(final String id) {
    return webClient.get()
        .uri(BROKEN_URL_TEMPLATE, id)
        .retrieve()
        .bodyToMono(User.class)
        .doOnError(error -> log.error("An error has occurred {}", error.getMessage()))
        .retryWhen(Retry.fixedDelay(MAX_RETRY_ATTEMPTS, Duration.ofMillis(DELAY_MILLIS)))
        .block();
  }

  public Mono<User> getUserWithRetryAsync(final String id) {
    return webClient.get()
        .uri(BROKEN_URL_TEMPLATE, id)
        .retrieve()
        .bodyToMono(User.class)
        .doOnError(error -> log.error("An error has occurred {}", error.getMessage()))
        .onErrorResume(
            e -> Mono.error(new RuntimeException("Something went wrong: " + e.getMessage())))
        .retryWhen(Retry.fixedDelay(MAX_RETRY_ATTEMPTS, Duration.ofMillis(DELAY_MILLIS)));
  }

  public User getUserWithFallback(final String id) {
    return webClient.get()
        .uri(BROKEN_URL_TEMPLATE, id)
        .retrieve()
        .bodyToMono(User.class)
        .doOnError(error -> log.error("An error has occurred {}", error.getMessage()))
        .onErrorResume(error -> Mono.just(new User()))
        .block();
  }

  public User getUserWithErrorHandling(final String id) {
    return webClient.get()
        .uri(BROKEN_URL_TEMPLATE, id)
        .retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals,
            error -> Mono.error(new RuntimeException("API not found")))
        .onStatus(HttpStatus.SERVICE_UNAVAILABLE::equals,
            error -> Mono.error(new RuntimeException("Server is not responding")))
        .bodyToMono(User.class)
        .block();
  }
}
