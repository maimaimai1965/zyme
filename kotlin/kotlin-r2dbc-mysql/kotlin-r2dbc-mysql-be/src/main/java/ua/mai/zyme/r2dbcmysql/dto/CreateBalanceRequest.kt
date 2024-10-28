package ua.mai.zyme.r2dbcmysql.dto

import java.time.LocalDateTime

data class CreateBalanceRequest @JvmOverloads constructor(
    val memberId: Int? = null,
    val amount: Long? = null,
    val createdDate: LocalDateTime? = null
)