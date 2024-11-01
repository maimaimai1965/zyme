package ua.mai.zyme.r2dbcmysql.webclient.log

import org.eclipse.jetty.client.Request
import org.eclipse.jetty.http.HttpField
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

class LogService {

    private val log = LoggerFactory.getLogger(LogService::class.java)

    /**
     * @param inboundRequest of type jetty client Request Object
     * @return return same request again after preparing logging
     */
    fun logRequestResponse(inboundRequest: Request, targetServiceName: String): Request {
        val logRequestBuilder = StringBuilder()
        val logResponseBuilder = StringBuilder()
        var first = true


        // Request Logging ---------------------------------------------------------------------------------------------

        /* This listener will be invoked when the request is being processed in order to be sent.
         */
        inboundRequest.onRequestBegin { request ->
            logRequestBuilder.append("WEBCLIENT REQ (to ")
                .append(targetServiceName)
                .append(")\n  Address: ").append(request.uri)
                .append("\n  HttpMethod: ").append(request.method)
        }

        /*  This listener will be invoked when the request headers are ready to be sent.
            When this is invoked we are appending Headers to string builder with proper format.
         */
        inboundRequest.onRequestHeaders { request ->
            if (log.isDebugEnabled) {
                if (request.headers.size() > 0) {
                    logRequestBuilder.append("\n  Headers: ")
                    for (header in request.headers) {
                        logRequestBuilder.append(header.name).append(":").append(header.value).append("; ")
                    }
                }
            }
        }

        inboundRequest.onRequestContent { _, content ->
            if (log.isDebugEnabled) {
                val bufferAsString = StandardCharsets.UTF_8.decode(content).toString()
                logRequestBuilder.append("\n  Body REQ:\n").append(bufferAsString)
            }
        }

        // Response Logging --------------------------------------------------------------------------------------------

        inboundRequest.onResponseBegin { response ->
            logResponseBuilder.append("WEBCLIENT RES (from ")
                .append(targetServiceName)
                .append(")\n  Address: ").append(response.request.uri)
                .append("\n  HttpMethod: ").append(response.request.method)
                .append("\n  Status: ").append(response.status)
        }

        inboundRequest.onResponseHeaders { response ->
            if (log.isDebugEnabled) {
                if (response.headers.size() > 0) {
                    logResponseBuilder.append("\n  Headers: ")
                    for (header in response.headers) {
                        logResponseBuilder.append(header.name).append(":").append(header.value).append("; ")
                    }
                }
            }
        }

        inboundRequest.onResponseContent { _, content ->
            if (log.isDebugEnabled) {
                val bufferAsString = StandardCharsets.UTF_8.decode(content).toString()
                if (first) {
                    logResponseBuilder.append("\n  Body RES:")
                    first = false
                }
                logResponseBuilder.append("\n").append(bufferAsString)
            }
        }

        inboundRequest.onResponseSuccess { response ->
            logInfoDebugMsg(logRequestBuilder.toString())
            val logResponse = logResponseBuilder.toString()
            if ("errorCd" in logResponse)
                log.error(logResponse)
            else
                logInfoDebugMsg(logResponseBuilder.toString())
        }

        inboundRequest.onResponseFailure { response, throwable ->
            val request = response.request
            if (logRequestBuilder.isEmpty()) {
                logRequestBuilder.append("WEBCLIENT REQ (to ")
                    .append(targetServiceName)
                    .append(")\n  Address: ").append(request.uri)
                    .append("\n  HttpMethod: ").append(request.method)
                if (log.isDebugEnabled) {
                    if (request.headers.size() > 0) {
                        logRequestBuilder.append("\n  Headers: ")
                        for (header in request.headers) {
                            logRequestBuilder.append(header.name).append(":").append(header.value).append("; ")
                        }
                    }
                }
            }
            logInfoDebugMsg(logRequestBuilder.toString())
            logErrorMsg(logResponseBuilder.toString(), throwable, response.request)
        }

        return inboundRequest
    }

    private fun logInfoDebugMsg(msg: String) {
        when {
            log.isDebugEnabled -> log.debug(msg)
            log.isInfoEnabled -> log.info(msg)
        }
    }

    private fun logErrorMsg(msg: String, throwable: Throwable, request: Request) {
        log.error(
            if (msg.isNotEmpty()) msg else "${throwable.message}; URI=${request.uri}",
            throwable
        )
    }

}