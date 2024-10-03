package ua.mai.zyme.r2dbcmysql.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.exception.FaultException;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
@Slf4j
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final MemberService memberService;

    @Transactional
    public Mono<Balance> insertBalance(Integer memberId, Long amount, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        return balanceRepository.insert(memberId, amount, createdDate, lastModifiedDate)
                .then(findBalanceByMemberId(memberId));
    }

    public Mono<Balance> insertZeroBalance(Integer memberId, LocalDateTime createdDate) {
        return insertBalance(memberId, 0L, createdDate, createdDate);
    }


    public Mono<Balance> findBalanceByMemberId(Integer memberId) {
        return balanceRepository.findById(memberId);
    }

    public Mono<Balance> findBalanceByMemberIdWithFaultWhenBalanceNotExists(Integer memberId) {
        return findBalanceByMemberId(memberId)
                .switchIfEmpty(Mono.error(new FaultException(AppFaultInfo.BALANCE_FOR_MEMBER_NOT_EXISTS, memberId)));
    }

    public Mono<Balance> findBalanceByMemberIdWithCreateZeroBalanceIfNotExists(Integer memberId, LocalDateTime createdDate) {
        return findBalanceByMemberId(memberId)
                .switchIfEmpty(insertZeroBalance(memberId, createdDate));
    }

}
