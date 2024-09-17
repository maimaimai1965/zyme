package ua.mai.zyme.r2dbcmysql.repository;

import ua.mai.zyme.r2dbcmysql.entity.Balance;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface BalanceRepository extends R2dbcRepository<Balance, Integer> {
  Mono<Balance> findByMemberId(Integer memberId);
}
