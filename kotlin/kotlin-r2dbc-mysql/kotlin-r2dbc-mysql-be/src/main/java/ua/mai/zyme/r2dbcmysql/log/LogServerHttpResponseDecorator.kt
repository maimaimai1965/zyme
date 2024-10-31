package ua.mai.zyme.r2dbcmysql.log

import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream

class LogServerHttpResponseDecorator(

    delegate: ServerHttpResponse,
    private val request: ServerHttpRequest,
    private val logger: Logger,
    private val mediaTypeFilter: MediaTypeFilter,
    private val formatter: LogMessageFormatter

) : ServerHttpResponseDecorator(delegate), WithMemoizingFunction {

    private val baos: ByteArrayOutputStream

    init {
        val mediaType = headers.contentType
        baos = if (logger.isDebugEnabled && mediaTypeFilter.logged(mediaType)) {
            ByteArrayOutputStream().also { stream ->
                delegate.beforeCommit {
                    flushLog(stream)
                    Mono.empty()
                }
            }
        } else if (logger.isInfoEnabled) {
            EMPTY_BYTE_ARRAY_OUTPUT_STREAM.also { stream ->
                delegate.beforeCommit {
                    flushLog(stream)
                    Mono.empty()
                }
            }
        } else {
            EMPTY_BYTE_ARRAY_OUTPUT_STREAM
        }
    }

    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
        return if (baos != EMPTY_BYTE_ARRAY_OUTPUT_STREAM) {
            super.writeWith(Flux.from(body).map(memoizingFunction(baos)))
        } else {
            super.writeWith(body)
        }
    }

    override fun writeAndFlushWith(body: Publisher<out Publisher<out DataBuffer>>): Mono<Void> {
        return if (baos != EMPTY_BYTE_ARRAY_OUTPUT_STREAM) {
            super.writeAndFlushWith(
                Flux.from(body).map { innerFlux ->
                    Flux.from(innerFlux).map(memoizingFunction(baos))
                }
            )
        } else {
            super.writeAndFlushWith(body)
        }
    }

    private fun flushLog(baos: ByteArrayOutputStream) {
        if (logger.isInfoEnabled) {
            if (logger.isDebugEnabled) {
                if (mediaTypeFilter.logged(headers.contentType)) {
                    logger.debug(formatter.format(request, delegate, baos.toByteArray()))
                } else {
                    logger.debug(formatter.format(request, delegate, null))
                }
            } else {
                logger.info(formatter.format(request, delegate, null))
            }
        }
    }

    override fun getLogger(): Logger = logger

    class DefaultLogMessageFormatter : LogMessageFormatter {
        override fun format(request: ServerHttpRequest, response: ServerHttpResponse, payload: ByteArray?): String {
            return buildString {
                append("REQ OUT [${request.id}]")
                append("\n  Address: ${request.uri}")
                append("\n  HttpMethod: ${request.method}")
                append("\n  Status: ${response.statusCode ?: HttpStatus.OK}")
                append("\n  Headers: ${request.headers}")
                if (payload != null) {
                    append("\n  Payload OUT: \n${String(payload)}")
                }
            }
        }
    }

    companion object {
        private val EMPTY_BYTE_ARRAY_OUTPUT_STREAM = ByteArrayOutputStream()
    }

}