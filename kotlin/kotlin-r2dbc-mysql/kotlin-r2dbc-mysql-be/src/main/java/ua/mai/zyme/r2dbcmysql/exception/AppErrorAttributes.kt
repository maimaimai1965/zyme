package ua.mai.zyme.r2dbcmysql.exception

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest

@Component
class AppErrorAttributes<T : Throwable> : DefaultErrorAttributes() {

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): Map<String, Any> {
        val errorAttributes = super.getErrorAttributes(request, options).toMutableMap()
        addErrorDetails(errorAttributes, request)
        return errorAttributes
    }

    protected fun addErrorDetails(errorAttributes: MutableMap<String, Any>, request: ServerRequest) {
        errorAttributes.remove("trace")
        val ex = getError(request)

        ex?.let {
            errorAttributes.remove("message")

            if (ex is FaultException) {
                errorAttributes.remove("status")

                errorAttributes["status"] = ex.httpStatus.value()
                errorAttributes["errorCd"] = ex.code
                errorAttributes["errorMsg"] = ex.message
                ex.causeMessage?.let { detailMsg ->
                    errorAttributes["detailMsg"] = detailMsg
                }
            } else {
                errorAttributes["errorCd"] = FaultInfo.UNEXPECTED_ERROR_CODE
                errorAttributes["errorMsg"] = FaultInfo.createMessageFor_UNEXPECTED_ERROR(ex.message ?: "Unknown error")
                ex.cause?.message?.let { causeMsg ->
                    errorAttributes["detailMsg"] = causeMsg
                }
            }
        }
    }

}