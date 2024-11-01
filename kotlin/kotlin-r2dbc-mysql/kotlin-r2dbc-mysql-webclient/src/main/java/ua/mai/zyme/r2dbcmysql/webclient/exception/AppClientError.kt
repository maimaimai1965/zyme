package ua.mai.zyme.r2dbcmysql.webclient.exception

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.web.reactive.function.client.ClientResponse

class AppClientError(

    clientResponse: ClientResponse,
    private val errorBody: String?

) : RuntimeException() {

    val clientFaultInfo: ClientFaultInfo

    init {
        clientFaultInfo = if (errorBody?.contains("\"errorCd\"") == true) {
            val objectMapper = ObjectMapper().apply {
                registerModule(JavaTimeModule())
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
            try {
                objectMapper.readValue(errorBody, ClientFaultInfo::class.java)
            } catch (e: JsonProcessingException) {
                ClientFaultInfo().apply {
                    path = clientResponse.request().uri.path
                    status = clientResponse.statusCode().value().toString()
                    errorMsg = e.message
                }
            }
        } else {
            ClientFaultInfo().apply {
                path = clientResponse.request().uri.path
                status = clientResponse.statusCode().value().toString()
            }
        }
    }

    override val message: String?
        get() = "AppClientError: " +
                addStr("Code", clientFaultInfo.errorCd) +
                addStr("Message", clientFaultInfo.errorMsg) +
                addStr("Path", clientFaultInfo.path) +
                addStr("RequestId", clientFaultInfo.requestId)

    companion object {
        private fun addStr(name: String, value: Any?): String {
            return if (value != null && value.toString() != null) "$name=$value; " else "$name"
        }
    }

}