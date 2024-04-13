package ua.mai.zyme.reactor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration(proxyBeanMethods = false)
public class GreetingRouter {

    @Bean
    public RouterFunction<ServerResponse> route(GreetingHandler greetingHandler) {

        return RouterFunctions
            .route(
                    RequestPredicates.GET("/hello").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                    greetingHandler::hello)
            .andRoute(
                    RequestPredicates.GET("/"),
                    serverRequest -> {
                        return ServerResponse
                                .ok()
                                .contentType(MediaType.TEXT_PLAIN)
                                .body(BodyInserters.fromValue("Main page"));
                    })
            .andRoute(  // /user , /user?name=
                    RequestPredicates.GET("/user"),
                    serverRequest -> {
                        String name = serverRequest.queryParam("name").orElse("Nobody");
                        return ServerResponse
                                .ok()
                                .contentType(MediaType.TEXT_PLAIN)
                                .body(BodyInserters.fromValue("Hello user " + name + "!"));
                    })
           .andRoute(
                   RequestPredicates.GET("/messages").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                   greetingHandler::messages)
        ;
    }
}
