package ua.mai.zyme.r2dbcmysql.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ua.mai.zyme.r2dbcmysql.util.AppUtil;
import ua.mai.zyme.r2dbcmysql.dto.CreateTransferRequest;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;
import ua.mai.zyme.r2dbcmysql.repository.TransferRepository;
import ua.mai.zyme.r2dbcmysql.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Slf4j
public class TransferController {

  private final TransferService transferService;
  private final TransferRepository transferRepository;

  @PostMapping
  @Transactional
  public Mono<Transfer> doTransfer(@RequestBody CreateTransferRequest request) throws ExecutionException, InterruptedException {
      return transferService.doTransfer(request.getFromMemberId(), request.getToMemberId(), request.getAmount(), AppUtil.now());
  }

  @GetMapping(value = "/{transferId}")
  public Mono<Transfer> findTransferByTransferId(@PathVariable Long transferId) {
    return transferRepository.findById(transferId);
  }

  @GetMapping(params = "fromMemberId")
  public Flux<Transfer> findTransfersByFromMemberId(@RequestParam Integer fromMemberId) {
    return transferRepository.findByFromMemberId(fromMemberId);
  }

  @GetMapping(params = "toMemberId")
  public Flux<Transfer> findTransfersByToMemberId(@RequestParam Integer toMemberId) {
    return transferRepository.findByToMemberId(toMemberId);
  }

}
