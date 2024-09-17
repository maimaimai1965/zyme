package ua.mai.zyme.r2dbcmysql.controller;

import ua.mai.zyme.r2dbcmysql.entity.Provider;
import ua.mai.zyme.r2dbcmysql.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/providers")
@RequiredArgsConstructor
@Slf4j
public class ProviderController {

  private final ProviderRepository providerRepository;

  @GetMapping
  public Flux<Provider> getAll() {
    return providerRepository.findAll();
  }

//  @GetMapping(value = "/{providerName}")
//  public Mono<Provider> getOne(@PathVariable String providerName) {
//    return providerRepository.findByProviderName(providerName);
//  }

  @PostMapping
  public Mono<Provider> createProvider(@RequestBody Provider provider) {
    return create(provider);
  }

  private Mono<Provider> create(Provider provider) {
//    providerRepository.create(provider.getProviderId(),
//            provider.getProviderName(),
//            provider.getProviderType(),
//            provider.getState(),
//            provider.getCreatedDt(),
//            provider.getEndDt());

    Mono.just(providerRepository.findByProviderId(provider.getProviderId()));

    // Т.к. данные provider при сохранении не изменяются:
    return Mono.just(provider);

//    // Если какое-то поле provider изменяется при сохранении в БД - вычитываем из БД.
//    return Mono.just(providerRepository.findByProviderId(provider.getProviderId()));
  }


  @GetMapping(value = "/{providerId}")
  public Mono<Provider> getOne(@PathVariable Integer providerId) {
    return providerRepository.findByProviderId(providerId);
  }

//  @PostMapping
//  public Flux<Provider> createProvider(@RequestBody Iterable<Provider> providers) {
//    return create(provider);
//  }



//  @PostMapping(value = "/{number}")
//  public Flux<Member> createMembers(@PathVariable int number) {
//    return generateRandomMember(number).subscribeOn(Schedulers.boundedElastic());
//  }

//  private Flux<Member> generateRandomMember(int number) {
//    return Mono.fromSupplier(
//            () -> Member.builder().name(RandomStringUtils.randomAlphabetic(5)).build())
//        .flatMap(providerRepository::save)
//        .repeat(number);
//  }

  @PutMapping
  public Mono<Provider> updateProvider(@RequestBody Provider provider) {
    return providerRepository
        .findByProviderId(provider.getProviderId())
        .flatMap(memberResult -> providerRepository.save(provider));
  }

  @DeleteMapping(value = "/{providerId}")
  public Mono<Void> deleteProvider(@PathVariable Integer providerId) {
    return providerRepository.deleteByProviderId(providerId);
  }


}
