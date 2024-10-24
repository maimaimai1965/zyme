package ua.mai.zyme.r2dbcmysql.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;

public interface TransferRepository extends R2dbcRepository<Transfer, Long> {

  Mono<Transfer> findByTransferId(Long transferId);

  Flux<Transfer> findByFromMemberId(Integer fromMemberId);

  Flux<Transfer> findByToMemberId(Integer toMemberId);

  Flux<Transfer> findByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId);

}
