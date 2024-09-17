package ua.mai.zyme.r2dbcmysql.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;

public interface TransferRepository extends R2dbcRepository<Transfer, Long> {
  Mono<Balance> findByTransferId(Long transferId);
}
