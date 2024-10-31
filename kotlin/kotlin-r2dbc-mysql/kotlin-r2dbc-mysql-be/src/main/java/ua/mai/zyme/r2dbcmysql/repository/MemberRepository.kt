package ua.mai.zyme.r2dbcmysql.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.mai.zyme.r2dbcmysql.entity.Member

interface MemberRepository : R2dbcRepository<Member, Int> {

    fun findByName(name: String): Mono<Member>

    fun findByNameLike(str: String): Flux<Member>

    @Query("SELECT * FROM member WHERE CHAR_LENGTH(name) <= :length")
    fun findByNameLengthLE(length: Int): Flux<Member>

    @Query("INSERT INTO member (name) VALUES (:name)")
    fun insertThroughSql(name: String): Mono<Void>

    @Query("UPDATE member SET name = :name WHERE member_id = :member_id")
    fun updateThroughSql(member_id: Int, name: String): Mono<Void>

    @Query("DELETE FROM member WHERE member_id = :member_id")
    fun deleteThroughSql(member_id: Int): Mono<Void>
}