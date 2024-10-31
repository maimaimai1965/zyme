package ua.mai.zyme.r2dbcmysql.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
class RequestLogWebFilter : WebFilter {

    private val logger: Logger = LoggerFactory.getLogger(RequestLogWebFilter::class.java)
    private var mediaTypeFilter: MediaTypeFilter = DEFAULT_MEDIA_FILTER
    private var logMessageFormatter: LogMessageFormatter = DEFAULT_LOG_MESSAGE_FORMATTER

    companion object {
        private val DEFAULT_MEDIA_FILTER: MediaTypeFilter = object : MediaTypeFilter {}
        private val DEFAULT_LOG_MESSAGE_FORMATTER: LogMessageFormatter = LogServerHttpRequestDecorator.DefaultLogMessageFormatter()
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return if (logger.isInfoEnabled) {
            chain.filter(decorate(exchange))
        } else {
            chain.filter(exchange)
        }
    }

    private fun decorate(exchange: ServerWebExchange): ServerWebExchange {
        val decoratedRequest = LogServerHttpRequestDecorator(
            exchange.request,
            exchange.response,
            logger,
            mediaTypeFilter,
            LogServerHttpRequestDecorator.DefaultLogMessageFormatter()
        )

        return object : ServerWebExchangeDecorator(exchange) {
            override fun getRequest(): ServerHttpRequest {
                return decoratedRequest
            }
        }
    }

}