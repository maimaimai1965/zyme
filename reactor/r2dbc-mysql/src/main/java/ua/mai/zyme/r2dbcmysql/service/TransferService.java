package ua.mai.zyme.r2dbcmysql.service;

import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;
import ua.mai.zyme.r2dbcmysql.repository.TransferRepository;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
@Slf4j
public class TransferService {

    private final TransferRepository transferRepository;
    private final BalanceRepository balanceRepository;
    private final BalanceService balanceService;

     /**
      * Если баланса у fromMemberId или toMemberId нет, то генерится исключение AppFaultInfo.BALANCE_FOR_MEMBER_NOT_EXISTS.
      */
    @Transactional
    public Mono<Transfer> doTransferWhenBalancesExist(Integer fromMemberId, Integer toMemberId, Long amount, LocalDateTime dateTime) {
        return Mono.zip(balanceService.findBalanceByMemberIdWithFaultWhenBalanceNotExists(fromMemberId),
                        balanceService.findBalanceByMemberIdWithFaultWhenBalanceNotExists(toMemberId))
                   .flatMap(balanceTuple -> executeTransfer(balanceTuple, amount, dateTime));

    }

    /**
     * Если баланса для fromMemberId не существует, то генерится исключение AppFaultInfo.BALANCE_FOR_MEMBER_NOT_EXISTS.
     * Если у toMemberId баланс не существует, но такой member есть, то для него создается нулевой баланс
     * и выполняется transfer. Если же такого toMemberId не существует, то генерится исключение
     * AppFaultInfo.MEMBER_NOT_EXISTS.
     */
    @Transactional
    public Mono<Transfer> doTransfer(Integer fromMemberId, Integer toMemberId, Long amount, LocalDateTime dateTime) {
        return Mono.zip(balanceService.findBalanceByMemberIdWithFaultWhenBalanceNotExists(fromMemberId),
                        balanceService.findBalanceByMemberIdWithCreateZeroBalanceIfNotExists(toMemberId, dateTime))
                .flatMap(memberTuple -> executeTransfer(memberTuple, amount, dateTime));
    }

    private Mono<Transfer> executeTransfer(Tuple2<Balance, Balance> balanceTuple, Long amount, LocalDateTime dateTime) {
        Balance balanceFrom = balanceTuple.getT1();
        Balance balanceTo = balanceTuple.getT2();
        return decreaseBalance(balanceFrom, amount, dateTime)
                .flatMap(fromBalanceResult -> increaseBalance(balanceTo, amount, dateTime))
                .flatMap(toBalanceResult -> transferRepository.save(
                        Transfer.builder()
                                .amount(amount)
                                .fromMemberId(balanceFrom.getMemberId())
                                .toMemberId(balanceTo.getMemberId())
                                .createdDate(dateTime)
                                .build()));
    }

    private Mono<Balance> decreaseBalance(Balance balance, Long delta, LocalDateTime dateTime) {
        balance.increaseBalance(-delta, dateTime);
        return balanceRepository.save(balance);
    }

    private Mono<Balance> increaseBalance(Balance balance, Long delta, LocalDateTime dateTime) {
        balance.increaseBalance(delta, dateTime);
        return balanceRepository.save(balance);
    }

}
