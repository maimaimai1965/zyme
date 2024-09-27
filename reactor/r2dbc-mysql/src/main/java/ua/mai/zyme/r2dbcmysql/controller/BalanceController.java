package ua.mai.zyme.r2dbcmysql.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(value = "/api/balances")
@RequiredArgsConstructor
@Slf4j
public class BalanceController {

  private final BalanceRepository balanceRepository;

  @GetMapping(value = "/{memberId}")
  public Mono<Balance> findBalanceByMemberId(@PathVariable Integer memberId) {
    return balanceRepository.findByMemberId(memberId);
  }

  // Пример url-а: /api/balances/?memberIds=521,523
  @GetMapping(value = "/")
  public Flux<Balance> findBalancesByMemberIds(@RequestParam List<Integer> memberIds) {
    return balanceRepository.findAllById(memberIds);
  }

  // Пример url-а: /api/balances/?minAmount=70&maxAmount=80
  @GetMapping(value = "/", params = {"minAmount", "maxAmount"})
  public Flux<Balance> findBalancesByAmountIsBetween(@RequestParam Long minAmount,
                                                     @RequestParam Long maxAmount) {
    return balanceRepository.findByAmountIsBetween(minAmount, maxAmount);
  }

}
