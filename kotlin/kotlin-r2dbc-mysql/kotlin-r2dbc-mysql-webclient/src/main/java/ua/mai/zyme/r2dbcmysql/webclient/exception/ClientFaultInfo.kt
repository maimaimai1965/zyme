package ua.mai.zyme.r2dbcmysql.webclient.exception

import java.time.LocalDateTime

data class ClientFaultInfo(
//    var timestamp: LocalDateTime? = null,
    var path: String? = null,
    var error: String? = null,
    var requestId: String? = null,
    var status: String? = null,
    var errorCd: String? = null,
    var errorMsg: String? = null
)