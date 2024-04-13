package ua.mai.zyme.reactor.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import ua.mai.zyme.reactor.domain.Message;

@RestController
@RequestMapping("/controller")
public class MainController {

    @GetMapping
    public Flux<Message> listFromController(@RequestParam(defaultValue = "0") Long start,
                                            @RequestParam(defaultValue = "3") Long count) {
        return Flux
                .just("Controller message 1", "Controller message 2", "Controller message 3", "Controller message 4")
                .skip(start)
                .take(count)
                .map(Message::new);
    }

}
