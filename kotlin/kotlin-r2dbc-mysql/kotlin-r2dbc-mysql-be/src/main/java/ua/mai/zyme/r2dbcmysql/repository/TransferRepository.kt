package ua.mai.zyme.r2dbcmysql.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.mai.zyme.r2dbcmysql.entity.Transfer

interface TransferRepository : R2dbcRepository<Transfer, Long> {

    fun findByTransferId(transferId: Long): Mono<Transfer>

    fun findByFromMemberId(fromMemberId: Int): Flux<Transfer>

    fun findByToMemberId(toMemberId: Int): Flux<Transfer>

    fun findByFromMemberIdAndToMemberId(fromMemberId: Int, toMemberId: Int): Flux<Transfer>

}