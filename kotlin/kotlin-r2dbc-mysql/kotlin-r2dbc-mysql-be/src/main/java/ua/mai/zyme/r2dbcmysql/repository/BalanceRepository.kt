package ua.mai.zyme.r2dbcmysql.repository

import org.springframework.data.r2dbc.repository.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.mai.zyme.r2dbcmysql.entity.Balance
import org.springframework.data.r2dbc.repository.R2dbcRepository
import java.time.LocalDateTime

interface BalanceRepository : R2dbcRepository<Balance, Int> {

    // Нет возможности вернуть Mono<Balance>.
    @Query("INSERT INTO balance (member_id, amount, created_date, last_modified_date) VALUES (:memberId, :amount, :createdDate, :lastModifiedDate)")
    fun insert(memberId: Int, amount: Long, createdDate: LocalDateTime, lastModifiedDate: LocalDateTime): Mono<Void>

    fun findByMemberId(memberId: Int): Mono<Balance>

    fun findByAmountIsBetween(minAmount: Long, maxAmount: Long): Flux<Balance>

}