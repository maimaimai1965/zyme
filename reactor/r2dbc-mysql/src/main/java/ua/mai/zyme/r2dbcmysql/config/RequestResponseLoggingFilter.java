package ua.mai.zyme.r2dbcmysql.config;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

/**
 * Логируется только запросы и ответы содержащие body!!!
 */
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@Component
public class RequestResponseLoggingFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest httpRequest = exchange.getRequest();
        final String httpUrl = httpRequest.getURI().toString();

        ServerHttpRequestDecorator loggingServerHttpRequestDecorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
//            String requestBody = "";

            @Override
            public Flux<DataBuffer> getBody() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                return super.getBody().doOnNext(dataBuffer -> {
                    try {
                        Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                        String body = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                        LOGGER.info("Request: payload={}", body);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            baos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        ServerHttpResponseDecorator loggingServerHttpResponseDecorator = new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Mono<DataBuffer> buffer = Mono.from(body);
                return super.writeWith(buffer.doOnNext(dataBuffer -> {
                    try {
                        Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                        String responseBody = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                        LOGGER.info("Response: payload={}", responseBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            baos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }));
            }
        };

        return chain.filter(exchange.mutate().request(loggingServerHttpRequestDecorator).response(loggingServerHttpResponseDecorator).build());
    }

}