package ua.mai.zyme.r2dbcmysql.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.exception.FaultException;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;

import java.time.LocalDateTime;


@Slf4j
@RequiredArgsConstructor
@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;

    @Transactional
    public Mono<Balance> insertBalance(Integer memberId, Long amount, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        return (amount < 0)
            ? Mono.error(new FaultException(AppFaultInfo.BALANCE_AMOUNT_CANNOT_BE_NEGATIVE))
            : balanceRepository.insert(memberId, amount, createdDate, lastModifiedDate)
                .onErrorMap(error ->
                        new FaultException(error, AppFaultInfo.BALANCE_NOT_CREATED, memberId, amount))
                .then(findBalanceByMemberId(memberId));
    }

    public Mono<Balance> insertZeroBalance(Integer memberId, LocalDateTime createdDate) {
        return balanceRepository.insert(memberId, 0L, createdDate, createdDate)
                .onErrorMap(error -> {
                     if (error instanceof DataIntegrityViolationException)
                         return new FaultException(error, AppFaultInfo.MEMBER_NOT_FOUND, memberId);
                     else
                         return new FaultException(error, AppFaultInfo.BALANCE_NOT_CREATED, memberId, 0);
                 })
                .then(findBalanceByMemberId(memberId));
    }


    public Mono<Balance> findBalanceByMemberId(Integer memberId) {
        return balanceRepository.findById(memberId);
    }

    public Mono<Balance> findBalanceByMemberIdWithFaultWhenBalanceNotExists(Integer memberId) {
        return findBalanceByMemberId(memberId)
                .switchIfEmpty(Mono.error(new FaultException(AppFaultInfo.BALANCE_FOR_MEMBER_NOT_FOUND, memberId)));
    }

    public Mono<Balance> findBalanceByMemberIdWithCreateZeroBalanceIfNotExists(Integer memberId, LocalDateTime createdDate) {
        return findBalanceByMemberId(memberId)
                .switchIfEmpty(insertZeroBalance(memberId, createdDate));
    }

}
