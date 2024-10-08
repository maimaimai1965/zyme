package ua.mai.zyme.r2dbcmysql.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface BalanceRepository extends R2dbcRepository<Balance, Integer> {

    // Нет возможности вернуть Mono<Balance>.
    @Query("INSERT INTO balance (member_id, amount, created_date, last_modified_date) VALUES (:memberId, :amount, :createdDate, :lastModifiedDate)")
    Mono<Void> insert(Integer memberId, Long amount, LocalDateTime createdDate, LocalDateTime lastModifiedDate);

    Mono<Balance> findByMemberId(Integer memberId);

    Flux<Balance> findByAmountIsBetween(Long minAmount, Long maxAmount);

}
