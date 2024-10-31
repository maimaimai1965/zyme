package ua.mai.zyme.r2dbcmysql.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Component
class ResponseLogWebFilter : WebFilter {

    private val logger: Logger = LoggerFactory.getLogger(ResponseLogWebFilter::class.java)
    private var mediaTypeFilter: MediaTypeFilter = DEFAULT_MEDIA_FILTER
    private var responseMessageFormatter: LogMessageFormatter = DEFAULT_LOG_MESSAGE_FORMATTER

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return if (logger.isInfoEnabled) {
            chain.filter(decorate(exchange))
        } else {
            chain.filter(exchange)
        }
    }

    private fun decorate(exchange: ServerWebExchange): ServerWebExchange {
        val decoratedResponse = LogServerHttpResponseDecorator(
            exchange.response,
            exchange.request,
            logger,
            mediaTypeFilter,
            LogServerHttpResponseDecorator.DefaultLogMessageFormatter()
        )

        return object : ServerWebExchangeDecorator(exchange) {
            override fun getResponse(): ServerHttpResponse = decoratedResponse
        }
    }

    companion object {
        private val DEFAULT_MEDIA_FILTER = object : MediaTypeFilter {}
        private val DEFAULT_LOG_MESSAGE_FORMATTER = LogServerHttpResponseDecorator.DefaultLogMessageFormatter()
    }

    fun getMediaTypeFilter(): MediaTypeFilter = mediaTypeFilter
    fun setMediaTypeFilter(mediaTypeFilter: MediaTypeFilter) {
        this.mediaTypeFilter = mediaTypeFilter
    }

    fun getResponseMessageFormatter(): LogMessageFormatter = responseMessageFormatter
    fun setResponseMessageFormatter(responseMessageFormatter: LogMessageFormatter) {
        this.responseMessageFormatter = responseMessageFormatter
    }

}