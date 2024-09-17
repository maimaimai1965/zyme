package ua.mai.zyme.r2dbcmysql.repository;

import org.springframework.data.r2dbc.repository.Query;
import ua.mai.zyme.r2dbcmysql.entity.Provider;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ProviderRepository extends R2dbcRepository<Provider, Integer> {

//  @Query("INSERT INTO cn_provider (PROVIDER_ID,PROVIDER_NAME,PROVIDER_TYPE,STATE,CREATED_DT,END_DT)" +
//         " VALUES (:providerId, :providerName, :providerType, :state, :createdDt, :endDt)")
//  void create(Integer providerId,
//              String providerName,
//              String providerType,
//              String state,
//              LocalDateTime createdDt,
//              LocalDateTime endDt);

//  Mono<Provider> create(Provider provider) {
//    return create(provider.getProviderId(),
//            provider.getProviderName(),
//            provider.getProviderType(),
//            provider.getState(),
//            provider.getCreatedDt(),
//            provider.getEndDt());
//  };

  Mono<Provider> findByProviderId(Integer providerId);
  Mono<Provider> findByProviderName(String providerName);
  Mono<Void> deleteByProviderId(Integer providerId);
}
