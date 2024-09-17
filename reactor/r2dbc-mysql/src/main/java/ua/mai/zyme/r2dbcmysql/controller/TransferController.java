package ua.mai.zyme.r2dbcmysql.controller;

import ua.mai.zyme.r2dbcmysql.dto.CreateTransactionWebRequest;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransferController {

  private final TransferService transferService;

  @PostMapping
  @Transactional
  public Mono<Balance> createTransfer(@RequestBody CreateTransactionWebRequest request) {
    return null;
//    return transferService.doTransfer(request);
  }

}
