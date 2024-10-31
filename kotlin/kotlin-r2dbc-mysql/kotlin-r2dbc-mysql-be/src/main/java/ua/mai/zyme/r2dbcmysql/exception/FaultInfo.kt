package ua.mai.zyme.r2dbcmysql.exception

import org.springframework.http.HttpStatus
import java.text.MessageFormat
import java.util.function.Supplier

interface FaultInfo : Supplier<FaultException> {

    companion object {
        val DEFAULT_SERVICE_FAULT_ERROR_HTTPSTATUS: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR  // 500

        const val UNEXPECTED_ERROR_CODE = "ERR001"
        const val UNEXPECTED_ERROR_TEMPLATE = "Unexpected error: {0}"
        val UNEXPECTED_ERROR_HTTPSTATUS: HttpStatus = DEFAULT_SERVICE_FAULT_ERROR_HTTPSTATUS

        const val NOT_FOUND_CODE = "ERR002"
        const val NOT_FOUND_TEMPLATE = "Resource {0} not found"
        val NOT_FOUND_HTTPSTATUS: HttpStatus = HttpStatus.NOT_FOUND

        fun createParamFor_NOT_FOUND(resourceName: String, resourceIdFieldName: String, resourceIdValue: String): String {
            return MessageFormat.format("{0} (for {1}={2})", resourceName, resourceIdFieldName, resourceIdValue)
        }

        fun createMessageFor_UNEXPECTED_ERROR(errorMessage: String): String {
            return MessageFormat.format(UNEXPECTED_ERROR_TEMPLATE, errorMessage)
        }
    }

    fun code(): String
    fun messageTemplate(): String
    fun httpStatus(): HttpStatus

    override fun get(): FaultException {
        return FaultException(this)
    }

}