package ua.mai.zyme.r2dbcmysql.dto

data class CreateTransferRequest @JvmOverloads constructor(
    val fromMemberId: Int? = null,
    val toMemberId: Int? = null,
    val amount: Long? = null
)
