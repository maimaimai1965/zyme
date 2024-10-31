package ua.mai.zyme.r2dbcmysql.log

import org.slf4j.Logger
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.http.server.reactive.ServerHttpResponse
import reactor.core.publisher.Flux
import java.io.ByteArrayOutputStream

class LogServerHttpRequestDecorator(

    delegate: ServerHttpRequest,
    private val response: ServerHttpResponse,
    private val logger: Logger,
    private val mediaTypeFilter: MediaTypeFilter,
    private val formatter: LogMessageFormatter

) : ServerHttpRequestDecorator(delegate), WithMemoizingFunction {

    private val decoratedBody: Flux<DataBuffer>

    init {
        this.decoratedBody = decorateBody(delegate.body)
        flushLog(EMPTY_BYTE_ARRAY_OUTPUT_STREAM, true) // getBody() не вызывается, если контроллер не нуждается в нём.
    }

    private fun decorateBody(body: Flux<DataBuffer>): Flux<DataBuffer> {
        val mediaType = headers.contentType
        return if (logger.isDebugEnabled && mediaTypeFilter.logged(mediaType)) {
            val baos = ByteArrayOutputStream()
            body.map(memoizingFunction(baos)).doOnComplete { flushLog(baos, false) }
        } else {
            body.doOnComplete { flushLog(EMPTY_BYTE_ARRAY_OUTPUT_STREAM, false) }
        }
    }

    override fun getBody(): Flux<DataBuffer> = decoratedBody

    private fun flushLog(baos: ByteArrayOutputStream, onCreate: Boolean) {
        if (logger.isInfoEnabled) {
            if (logger.isDebugEnabled) {
                if (mediaTypeFilter.logged(headers.contentType)) {
                    logger.debug(formatter.format(delegate, response, if (onCreate) null else baos.toByteArray()))
                } else {
                    logger.debug(formatter.format(delegate, response, null))
                }
            } else if (onCreate) {
                logger.info(formatter.format(delegate, response, null))
            }
        }
    }

    override fun getLogger(): Logger = logger

    class DefaultLogMessageFormatter : LogMessageFormatter {
        override fun format(request: ServerHttpRequest, response: ServerHttpResponse, payload: ByteArray?): String {
            return buildString {
                if (payload == null) {
                    append("REQ IN [${request.id}]")
                    append("\n  Address: ${request.uri}")
                    append("\n  HttpMethod: ${request.method}")
                    append("\n  Headers: ${request.headers}")
                } else {
                    append("\n  Payload IN: \n${String(payload)}")
                }
            }
        }
    }

    companion object {
        private val EMPTY_BYTE_ARRAY_OUTPUT_STREAM = ByteArrayOutputStream()
    }

}