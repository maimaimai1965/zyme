package ua.mai.zyme.r2dbcmysql.controller;

import ua.mai.zyme.r2dbcmysql.dto.CreateBalanceWebRequest;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/balance")
@RequiredArgsConstructor
@Slf4j
public class BalanceController {

  private final BalanceRepository balanceRepository;

  @PostMapping
  public Mono<Balance> createBalance(@RequestBody CreateBalanceWebRequest request) {
    final Balance balance = Balance.builder()
        .amount(request.getBalance())
        .memberId(request.getMemberId())
        .build();
    return balanceRepository.save(balance);
  }

}
