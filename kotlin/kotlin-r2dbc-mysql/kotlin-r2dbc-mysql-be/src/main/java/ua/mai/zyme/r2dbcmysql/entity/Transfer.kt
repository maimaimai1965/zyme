package ua.mai.zyme.r2dbcmysql.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime

data class Transfer @JvmOverloads constructor(
    @Id
    @Column("transfer_id")
    var transferId: Long? = null,

    @Column("from_member_id")
    var fromMemberId: Int? = null,

    @Column("to_member_id")
    var toMemberId: Int? = null,

    var amount: Long? = null,

    @Column("created_date")
    var createdDate: LocalDateTime? = null
)