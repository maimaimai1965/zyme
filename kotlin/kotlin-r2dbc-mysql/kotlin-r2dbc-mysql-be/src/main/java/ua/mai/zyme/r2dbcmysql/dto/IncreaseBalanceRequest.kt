package ua.mai.zyme.r2dbcmysql.dto

import java.time.LocalDateTime

data class IncreaseBalanceRequest(
    var memberId: Int? = null,
    var amount: Long? = null,
    var modifiedDate: LocalDateTime? = null
)