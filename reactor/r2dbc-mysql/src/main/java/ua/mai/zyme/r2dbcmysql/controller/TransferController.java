package ua.mai.zyme.r2dbcmysql.controller;

import ua.mai.zyme.r2dbcmysql.config.AppUtil;
import ua.mai.zyme.r2dbcmysql.dto.CreateTransferRequest;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;
import ua.mai.zyme.r2dbcmysql.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Slf4j
public class TransferController {

  private final TransferService transferService;

  @PostMapping
  @Transactional
  public Mono<Transfer> doTransfer(@RequestBody CreateTransferRequest request) throws ExecutionException, InterruptedException {
      return transferService.doTransferWhenBalancesExist(request.getFromMemberId(), request.getToMemberId(), request.getAmount(), AppUtil.now());
  }

}
