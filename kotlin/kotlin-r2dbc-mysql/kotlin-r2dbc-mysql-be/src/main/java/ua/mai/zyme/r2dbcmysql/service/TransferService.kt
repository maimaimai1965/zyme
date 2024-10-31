package ua.mai.zyme.r2dbcmysql.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import ua.mai.zyme.r2dbcmysql.entity.Balance
import ua.mai.zyme.r2dbcmysql.entity.Transfer
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo
import ua.mai.zyme.r2dbcmysql.exception.FaultException
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository
import ua.mai.zyme.r2dbcmysql.repository.TransferRepository
import java.time.LocalDateTime

@Service
open class TransferService(
    private val transferRepository: TransferRepository,
    private val balanceRepository: BalanceRepository,
    private val balanceService: BalanceService
) {

    /**
     * Если баланса для fromMemberId не существует, то генерится исключение AppFaultInfo.BALANCE_FOR_MEMBER_NOT_EXISTS.
     * Если у toMemberId баланс не существует, но такой member есть, то для него создается нулевой баланс
     * и выполняется transfer. Если же такого toMemberId не существует, то генерится исключение
     * AppFaultInfo.MEMBER_NOT_EXISTS.
     */
    @Transactional
    open fun doTransfer(fromMemberId: Int, toMemberId: Int, amount: Long, dateTime: LocalDateTime): Mono<Transfer> {
        return Mono.zip(
            balanceService.findBalanceByMemberIdWithFaultWhenBalanceNotExists(fromMemberId),
            balanceService.findBalanceByMemberIdWithCreateZeroBalanceIfNotExists(toMemberId, dateTime)
        ).flatMap { memberTuple -> executeTransfer(memberTuple, amount, dateTime) }
    }

    /**
     * Если баланса у fromMemberId или toMemberId нет, то генерится исключение AppFaultInfo.BALANCE_FOR_MEMBER_NOT_EXISTS.
     */
    @Transactional
    open fun doTransferWhenBalancesExist(fromMemberId: Int, toMemberId: Int, amount: Long, dateTime: LocalDateTime): Mono<Transfer> {
        return Mono.zip(
            balanceService.findBalanceByMemberIdWithFaultWhenBalanceNotExists(fromMemberId),
            balanceService.findBalanceByMemberIdWithFaultWhenBalanceNotExists(toMemberId)
        ).flatMap { balanceTuple -> executeTransfer(balanceTuple, amount, dateTime) }
    }

    private fun executeTransfer(balanceTuple: Tuple2<Balance, Balance>, amount: Long, dateTime: LocalDateTime): Mono<Transfer> {
        if (amount <= 0) {
            return Mono.error(FaultException(AppFaultInfo.TRANSFER_AMOUNT_MUST_BE_POSITIVE))
        }
        val balanceFrom = balanceTuple.t1
        val balanceTo = balanceTuple.t2
        return decreaseBalance(balanceFrom, amount, dateTime)
            .flatMap { increaseBalance(balanceTo, amount, dateTime) }
            .flatMap {
                transferRepository.save(
                    Transfer(null, balanceFrom.memberId, balanceTo.memberId, amount, dateTime)
                )
            }
    }

    private fun decreaseBalance(balance: Balance, delta: Long, dateTime: LocalDateTime): Mono<Balance> {
        balance.increaseBalance(-delta, dateTime)
        return balanceRepository.save(balance)
    }

    private fun increaseBalance(balance: Balance, delta: Long, dateTime: LocalDateTime): Mono<Balance> {
        balance.increaseBalance(delta, dateTime)
        return balanceRepository.save(balance)
    }

}