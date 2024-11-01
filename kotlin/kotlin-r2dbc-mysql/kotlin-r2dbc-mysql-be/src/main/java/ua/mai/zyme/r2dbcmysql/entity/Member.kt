package ua.mai.zyme.r2dbcmysql.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column

data class Member @JvmOverloads constructor(
    @Id
    @Column("member_id")
    var memberId: Int? = null,

    var name: String
)