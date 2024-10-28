package ua.mai.zyme.r2dbcmysql.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import ua.mai.zyme.r2dbcmysql.exception.FaultException
import java.time.LocalDateTime
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo.*

data class Balance @JvmOverloads constructor(
    @Id
    @Column("member_id")
    var memberId: Int? = null,

    var amount: Long? = null,

    @Column("created_date")
    var createdDate: LocalDateTime? = null,

    @Column("last_modified_date")
    var lastModifiedDate: LocalDateTime? = null
) {
    fun increaseBalance(delta: Long, modifiedDate: LocalDateTime): Long {
        val newAmount = (amount ?: 0) + delta
        if (newAmount < 0) {
            throw FaultException(BALANCE_AMOUNT_NOT_ENOUGH, memberId, amount, delta)
        }
        if (modifiedDate.isBefore(lastModifiedDate ?: modifiedDate)) {
            throw FaultException(BALANCE_CHANGE_DATE_EARLIER_LAST_DATE, memberId, lastModifiedDate, modifiedDate)
        }
        amount = newAmount
        lastModifiedDate = modifiedDate
        return newAmount
    }
}