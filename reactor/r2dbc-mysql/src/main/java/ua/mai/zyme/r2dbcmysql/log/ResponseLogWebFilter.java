package ua.mai.zyme.r2dbcmysql.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Component
public class ResponseLogWebFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseLogWebFilter.class);

    private static final MediaTypeFilter DEFAULT_MEDIA_FILTER = new MediaTypeFilter() {};
    private static final LogMessageFormatter DEFAULT_LOG_MESSAGE_FORMATTER = new LogServerHttpResponseDecorator.DefaultLogMessageFormatter();

    private Logger logger;

    private MediaTypeFilter mediaTypeFilter;

    private LogMessageFormatter responseMessageFromatter;

    public ResponseLogWebFilter() {
        this.logger = LOGGER;
        this.mediaTypeFilter = DEFAULT_MEDIA_FILTER;
        this.responseMessageFromatter = DEFAULT_LOG_MESSAGE_FORMATTER;
    }

//    public MediaTypeFilter getMediaTypeFilter() {
//        return mediaTypeFilter;
//    }
//
//    public void setMediaTypeFilter(MediaTypeFilter mediaTypeFilter) {
//        this.mediaTypeFilter = mediaTypeFilter;
//    }
//
//    public LogMessageFormatter getResponseMessageFromatter() {
//        return responseMessageFromatter;
//    }
//
//    public void setResponseMessageFromatter(LogMessageFormatter responseMessageFromatter) {
//        this.responseMessageFromatter = responseMessageFromatter;
//    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (logger.isInfoEnabled()) {
            return chain.filter(decorate(exchange));
        } else {
            return chain.filter(exchange);
        }
    }

    private ServerWebExchange decorate(ServerWebExchange exchange) {

        final ServerHttpResponse decoratedResponse = new LogServerHttpResponseDecorator(
                exchange.getResponse(),
                exchange.getRequest(),
                logger,
                mediaTypeFilter,
                new LogServerHttpResponseDecorator.DefaultLogMessageFormatter()
        );

        return new ServerWebExchangeDecorator(exchange) {

            @Override
            public ServerHttpResponse getResponse() {
                return decoratedResponse;
            }

        };
    }

}