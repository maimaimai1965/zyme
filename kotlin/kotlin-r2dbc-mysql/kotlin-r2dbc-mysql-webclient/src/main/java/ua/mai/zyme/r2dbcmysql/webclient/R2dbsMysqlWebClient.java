package ua.mai.zyme.r2dbcmysql.webclient;

import ua.mai.zyme.r2dbcmysql.webclient.property.WebClientProperty;
import ua.mai.zyme.r2dbcmysql.webclient.exception.AppClientError;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.dto.CreateTransferRequest;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;
import ua.mai.zyme.r2dbcmysql.webclient.log.LogService;

import java.net.URI;
import java.util.List;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.JettyClientHttpConnector;

// Reactor Netty
//import io.netty.channel.ChannelOption;
//import io.netty.handler.timeout.ReadTimeoutHandler;
//import io.netty.handler.timeout.WriteTimeoutHandler;
//import reactor.netty.http.client.HttpClient;
//import java.util.concurrent.TimeUnit;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;

public class R2dbsMysqlWebClient {

    private final WebClient webClient;

    static final String MEMBER_BASE_URL = "/api/members";
    static final String BALANCE_BASE_URL = "/api/balances";
    static final String TRANSFER_BASE_URL = "/api/transfers";

    private static int DEFAULT_MAX_RETRY_ATTEMPTS = 3;
    private static int DEFAULT_DELAY_MILLIS = 100;

    private int maxRetryAttempts = DEFAULT_MAX_RETRY_ATTEMPTS;
    private int delayMillis = DEFAULT_DELAY_MILLIS;


    public R2dbsMysqlWebClient(WebClientProperty webClientProperty) {
        webClient = webClientWithJetty(webClientProperty, new LogService());
//        webClient = webClientWithNetty(webClientProperty, new LogService());
    }

    private WebClient webClientWithJetty(WebClientProperty webClientProperty, LogService logService) {
        // Настраиваем Jetty HttpClient
        HttpClient httpClient = new HttpClient() {
            @Override
            public Request newRequest(URI uri) {
                Request request = super.newRequest(uri);
                return logService.logRequestResponse(request, webClientProperty.getTargetServiceName());
            }
        };

        httpClient.setConnectTimeout(webClientProperty.getConnectTimeout());
//        httpClient.setFollowRedirects(false);
        try {
            httpClient.start();  // Jetty HttpClient должен быть запущен
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при запуске Jetty HttpClient", e);
        }

        // Используем JettyClientHttpConnector для WebClient
        ClientHttpConnector jettyConnector = new JettyClientHttpConnector(httpClient);

        return WebClient.builder()
                .clientConnector(jettyConnector)  // Указываем JettyConnector
                .baseUrl(webClientProperty.getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .filter(errorHandlingFilter())
                .build();
    }

//    public WebClient webClientWithNetty() {
//        final var httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
//                .doOnConnected(connection -> {
//                    connection.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS));
//                    connection.addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS));
//                });
//
//        return WebClient.builder()
//                  .baseUrl(baseUrl)
//                  .clientConnector(new ReactorClientHttpConnector(httpClient)).build();
//    }

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }
    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public int getDelayMillis() {
        return delayMillis;
    }
    public void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    // Фильтр для обработки ошибок
    private ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                // Обрабатываем ошибку в зависимости от статуса
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody ->
                                Mono.error(new AppClientError(clientResponse, errorBody))
                         );
            }
            // Если ошибки нет, продолжаем работу с ответом
            return Mono.just(clientResponse);
        });
    }


    // ------------------------------------ MemberController -----------------------------------------------------------

    public Mono<Member> insertMember(Mono<Member> monoMember) {
        return webClient.post()
                .uri(MEMBER_BASE_URL)
                .body(monoMember, Member.class)
                .retrieve()
                .bodyToMono(Member.class);
    }

    public Flux<Member> insertMembers(Flux<Member> fluxMember) {
        return webClient.post()
                .uri(MEMBER_BASE_URL + "/flux")
                .body(fluxMember, Member.class)
                .retrieve()
                .bodyToFlux(Member.class);
    }

    public Mono<Member> updateMember(Mono<Member> monoMember) {
        return webClient.put()
                .uri(MEMBER_BASE_URL)
                .body(monoMember, Member.class)
                .retrieve()
                .bodyToMono(Member.class);
    }

    public Mono<Void> deleteMemberById(Integer id) {
        return webClient.delete()
                .uri(MEMBER_BASE_URL + "/" + id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Flux<Member> findMembersAll() {
        return webClient.get()
                .uri(MEMBER_BASE_URL)
                .retrieve()
                .bodyToFlux(Member.class);
    }

    public Mono<Member> findMemberByMemberId(Integer id) {
        return webClient.get()
                .uri(MEMBER_BASE_URL + "/{id}", id)
                .retrieve()
                .bodyToMono(Member.class);
    }


    public Mono<Member> findMemberByName(String name) {
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(MEMBER_BASE_URL)
                                .queryParam("name", name)
                                .build())
                .retrieve()
                .bodyToMono(Member.class);
    }

    public Flux<Member> findMembersByNameLike(String nameLike) {
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(MEMBER_BASE_URL)
                                .queryParam("nameLike", nameLike)
                                .build())
                .retrieve()
                .bodyToFlux(Member.class);
    }


    // ------------------------------------ BalanceController ----------------------------------------------------------

    public Mono<Balance> findBalanceByMemberId(Integer memberId) {
        return webClient.get()
                .uri(BALANCE_BASE_URL + "/{id}", memberId)
                .retrieve()
                .bodyToMono(Balance.class);
    }

    public Flux<Balance> findBalancesByMemberIds(List<Integer> memberIds) {
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(BALANCE_BASE_URL)
                                .queryParam("memberIds", memberIds)
                                .build())
                .retrieve()
                .bodyToFlux(Balance.class);
    }

    public Flux<Balance> findBalancesByAmountIsBetween(Long minAmount, Long maxAmount) {
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(BALANCE_BASE_URL)
                                .queryParam("minAmount", minAmount)
                                .queryParam("maxAmount", maxAmount)
                                .build())
                .retrieve()
                .bodyToFlux(Balance.class);
    }

    // ------------------------------------ TransferController ---------------------------------------------------------

    public Mono<Transfer> doTransfer(CreateTransferRequest request) {
        return webClient.post()
                .uri(TRANSFER_BASE_URL)
                .body(Mono.just(request), CreateTransferRequest.class)
                .retrieve()
                .bodyToMono(Transfer.class);
    }

    public Mono<Transfer> findTransferByTransferId(Long transferId) {
        return webClient.get()
                .uri(TRANSFER_BASE_URL + "/{id}", transferId)
                .retrieve()
                .bodyToMono(Transfer.class);
    }

    public Flux<Transfer> findTransfersByFromMemberId(Integer fromMemberId) {
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(TRANSFER_BASE_URL)
                                .queryParam("fromMemberId", fromMemberId)
                                .build())
                .retrieve()
                .bodyToFlux(Transfer.class);
    }

    public Flux<Transfer> findTransfersByToMemberId(Integer toMemberId) {
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(TRANSFER_BASE_URL)
                                .queryParam("toMemberId", toMemberId)
                                .build())
                .retrieve()
                .bodyToFlux(Transfer.class);
    }

}
