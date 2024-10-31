package ua.mai.zyme.r2dbcmysql.controller

import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.mai.zyme.r2dbcmysql.entity.Balance
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository

@RestController
@RequestMapping("/api/balances")
class BalanceController(
    private val balanceRepository: BalanceRepository
) {

    @GetMapping("/{memberId}")
    fun findBalanceByMemberId(@PathVariable memberId: Int): Mono<Balance> {
        return balanceRepository.findByMemberId(memberId)
    }

    // Пример URL: /api/balances/?memberIds=521,523
    @GetMapping
    fun findBalancesByMemberIds(@RequestParam memberIds: List<Int>): Flux<Balance> {
        return balanceRepository.findAllById(memberIds)
    }

    // Пример URL: /api/balances/?minAmount=70&maxAmount=80
    @GetMapping(params = ["minAmount", "maxAmount"])
    fun findBalancesByAmountIsBetween(
        @RequestParam minAmount: Long,
        @RequestParam maxAmount: Long
    ): Flux<Balance> {
        return balanceRepository.findByAmountIsBetween(minAmount, maxAmount)
    }

}