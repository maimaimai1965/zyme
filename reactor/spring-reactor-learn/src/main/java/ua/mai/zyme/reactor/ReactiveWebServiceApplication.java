package ua.mai.zyme.reactor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * URL-ы, доступные в браузере:
 *  http://localhost:8080/
 *  http://localhost:8080/hello
 *  http://localhost:8080/user?name=Oleg
 *  http://localhost:8080/messages
 *  http://localhost:8080/messages?start=0&count=2
 *  http://localhost:8080/controller
 *  http://localhost:8080/controller?start=0&count=2
 */
@SpringBootApplication
public class ReactiveWebServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ReactiveWebServiceApplication.class, args);

        GreetingClient greetingClient = context.getBean(GreetingClient.class);

        // We need to block for the content here or the JVM might exit before the message is logged
        System.out.println("/ -> " + greetingClient.getMainPage().block());
        System.out.println("/hello -> " + greetingClient.getHello().block().getText());
        System.out.println("/user -> " + greetingClient.getUser(null).block());
        System.out.println("/user?name=Oleg -> " + greetingClient.getUser("Oleg").block());
        System.out.println("/messages -> " + greetingClient.getMessages().collectList().block());

    }
}
