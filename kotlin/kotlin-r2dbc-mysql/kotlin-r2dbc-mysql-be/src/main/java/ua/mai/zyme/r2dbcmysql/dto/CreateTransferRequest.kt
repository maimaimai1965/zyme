package ua.mai.zyme.r2dbcmysql.dto

data class CreateTransferRequest @JvmOverloads constructor(
    val fromMemberId: Int,
    val toMemberId: Int,
    val amount: Long
)
