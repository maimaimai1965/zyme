package ua.mai.zyme.r2dbcmysql.exception

import org.apache.commons.logging.Log
import org.springframework.boot.autoconfigure.web.ErrorProperties
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpLogging
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

class AppErrorWebExceptionHandler(
        errorAttributes: ErrorAttributes,
        resources: WebProperties.Resources,
        errorProperties: ErrorProperties,
        applicationContext: ApplicationContext
) : DefaultErrorWebExceptionHandler(errorAttributes, resources, errorProperties, applicationContext) {

    companion object {
        private val logger: Log = HttpLogging.forLogName(AbstractErrorWebExceptionHandler::class.java)

        // Атрибуты ошибки и их порядок в логе
        private val attributesInErrorLog = listOf("requestId", "errorCd", "errorMsg", "detailMsg", "status", "path", "timestamp")

        private val attributeQuotationMarks = mapOf(
            "errorMsg" to "\"",
            "detailMsg" to "\"",
            "error" to "\""
        )
    }

    override fun logError(request: ServerRequest, response: ServerResponse, throwable: Throwable) {
        val errorAttributes = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL))
        if (errorAttributes.containsKey("errorCd")) {
            logger.error(formatAppError(throwable, request, errorAttributes))
        } else {
            super.logError(request, response, throwable)
        }
    }

    private fun formatAppError(ex: Throwable, request: ServerRequest, errorAttributes: Map<String, Any>): String {
        val reason = ex::class.simpleName + ": " +
                attributesInErrorLog
                    .filter {
                        it in attributesInErrorLog &&
                        it in errorAttributes
                     }
                    .joinToString(", ", "{", "}") {
                        key -> infoForElement(errorAttributes, key)
                     }
        return "Resolved [$reason] for HTTP ${request.method()} ${request.path()}"
    }

    private fun infoForElement(errorAttributes: Map<String, Any>, key: String): String {
        return "$key=${getAttributeQuotation(key)}${errorAttributes[key]}${getAttributeQuotation(key)}"
    }

    private fun getAttributeQuotation(key: String): String {
        return attributeQuotationMarks[key] ?: ""
    }

}