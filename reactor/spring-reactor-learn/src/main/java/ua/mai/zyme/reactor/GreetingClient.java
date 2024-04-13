package ua.mai.zyme.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ua.mai.zyme.reactor.domain.Message;

@Component
public class GreetingClient {
    private final WebClient client;

    // Spring Boot auto-configures a `WebClient.Builder` instance with nice defaults and customizations.
    // We can use it to create a dedicated `WebClient` for our component.
    public GreetingClient(WebClient.Builder builder) {
        this.client = builder.baseUrl("http://localhost:8080").build();
    }

    public Mono<Greeting> getHello() {
        return this.client.get()
                .uri("/hello")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Greeting.class);
//                .map(Greeting::getText);
    }

    public Mono<String> getMainPage() {
        return this.client.get()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
//                .map(String::toString);
    }

    public Mono<String> getUser(String name) {
        return this.client.get()
                .uri("/user" + (name == null || name.isEmpty() ? "" : "?name=" + name))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
//                .map(String::toString);
    }

    public Flux<Message> getMessages() {
        return this.client.get()
                .uri("/messages")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Message.class);
    }

}
