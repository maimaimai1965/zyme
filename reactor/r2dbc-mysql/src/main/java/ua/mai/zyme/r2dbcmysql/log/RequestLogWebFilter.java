package ua.mai.zyme.r2dbcmysql.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class RequestLogWebFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogWebFilter.class);

    private static final MediaTypeFilter DEFAULT_MEDIA_FILTER = new MediaTypeFilter() {};
    private static final LogMessageFormatter DEFAULT_LOG_MESSAGE_FORMATTER = new LogServerHttpRequestDecorator.DefaultLogMessageFormatter();

    private Logger logger;

    private MediaTypeFilter mediaTypeFilter;

    private LogMessageFormatter logMessageFormatter;

    public RequestLogWebFilter() {
        this.logger = LOGGER;
        this.mediaTypeFilter = DEFAULT_MEDIA_FILTER;
        this.logMessageFormatter = DEFAULT_LOG_MESSAGE_FORMATTER;
    }

    public MediaTypeFilter getMediaTypeFilter() {
        return mediaTypeFilter;
    }

    public void setMediaTypeFilter(MediaTypeFilter mediaTypeFilter) {
        this.mediaTypeFilter = mediaTypeFilter;
    }

    public LogMessageFormatter getLogMessageFormatter() {
        return logMessageFormatter;
    }

    public void setLogMessageFormatter(LogMessageFormatter logMessageFormatter) {
        this.logMessageFormatter = logMessageFormatter;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (logger.isInfoEnabled()) {
            return chain.filter(decorate(exchange));
        } else {
            return chain.filter(exchange);
        }
    }

    private ServerWebExchange decorate(ServerWebExchange exchange) {

        final ServerHttpRequest decoratedRequest = new LogServerHttpRequestDecorator(
                exchange.getRequest(),
                exchange.getResponse(),
                logger,
                mediaTypeFilter,
                new LogServerHttpRequestDecorator.DefaultLogMessageFormatter()
        );

        return new ServerWebExchangeDecorator(exchange) {

            @Override
            public ServerHttpRequest getRequest() {
                return decoratedRequest;
            }

        };
    }

}