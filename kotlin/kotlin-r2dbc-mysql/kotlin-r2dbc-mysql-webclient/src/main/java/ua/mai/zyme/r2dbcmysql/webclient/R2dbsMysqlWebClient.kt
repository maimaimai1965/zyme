package ua.mai.zyme.r2dbcmysql.webclient

import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.mai.zyme.r2dbcmysql.dto.CreateTransferRequest
import ua.mai.zyme.r2dbcmysql.entity.Balance
import ua.mai.zyme.r2dbcmysql.entity.Member
import ua.mai.zyme.r2dbcmysql.entity.Transfer
import ua.mai.zyme.r2dbcmysql.webclient.log.LogService
import ua.mai.zyme.r2dbcmysql.webclient.property.WebClientProperty
import ua.mai.zyme.r2dbcmysql.webclient.exception.AppClientError
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.client.Request
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.JettyClientHttpConnector
import java.net.URI

class R2dbsMysqlWebClient(

    webClientProperty: WebClientProperty

) {

    private val webClient: WebClient = webClientWithJetty(webClientProperty, LogService())

    companion object {
        const val MEMBER_BASE_URL = "/api/members"
        const val BALANCE_BASE_URL = "/api/balances"
        const val TRANSFER_BASE_URL = "/api/transfers"
        const val DEFAULT_MAX_RETRY_ATTEMPTS = 3
        const val DEFAULT_DELAY_MILLIS = 100
    }

    var maxRetryAttempts: Int = DEFAULT_MAX_RETRY_ATTEMPTS
    var delayMillis: Int = DEFAULT_DELAY_MILLIS

    private fun webClientWithJetty(webClientProperty: WebClientProperty, logService: LogService): WebClient {
        val httpClient = object : HttpClient() {
            override fun newRequest(uri: URI): Request {
                val request = super.newRequest(uri)
                return logService.logRequestResponse(request, webClientProperty.targetServiceName?: "unknown")
            }
        }

        httpClient.connectTimeout = webClientProperty.connectTimeout.toLong()
        try {
            httpClient.start()
        } catch (e: Exception) {
            throw RuntimeException("Ошибка при запуске Jetty HttpClient", e)
        }

        val jettyConnector: ClientHttpConnector = JettyClientHttpConnector(httpClient)

        return WebClient.builder()
            .clientConnector(jettyConnector)
            .baseUrl(webClientProperty.baseUrl)
            .defaultHeader("Content-Type", "application/json")
            .filter(errorHandlingFilter())
            .build()
    }

    private fun errorHandlingFilter(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
            if (clientResponse.statusCode().isError) {
                clientResponse.bodyToMono(String::class.java)
                    .flatMap { errorBody -> Mono.error(AppClientError(clientResponse, errorBody)) }
            } else {
                Mono.just(clientResponse)
            }
        }
    }

    fun insertMember(monoMember: Mono<Member>): Mono<Member> =
        webClient
            .post()
            .uri(MEMBER_BASE_URL)
            .body(monoMember, Member::class.java)
            .retrieve()
            .bodyToMono(Member::class.java)

    fun insertMembers(fluxMember: Flux<Member>): Flux<Member> =
        webClient
            .post()
            .uri("$MEMBER_BASE_URL/flux")
            .body(fluxMember, Member::class.java)
            .retrieve()
            .bodyToFlux(Member::class.java)

    fun updateMember(monoMember: Mono<Member>): Mono<Member> =
        webClient
            .put()
            .uri(MEMBER_BASE_URL)
            .body(monoMember, Member::class.java)
            .retrieve()
            .bodyToMono(Member::class.java)

    fun deleteMemberById(id: Int): Mono<Void> =
        webClient
            .delete()
            .uri("$MEMBER_BASE_URL/$id")
            .retrieve()
            .bodyToMono(Void::class.java)

    fun findMembersAll(): Flux<Member> =
        webClient
            .get()
            .uri(MEMBER_BASE_URL)
            .retrieve()
            .bodyToFlux(Member::class.java)

    fun findMemberByMemberId(id: Int): Mono<Member> =
        webClient
            .get()
            .uri("$MEMBER_BASE_URL/{id}", id)
            .retrieve()
            .bodyToMono(Member::class.java)

    fun findMemberByName(name: String): Mono<Member> =
        webClient
            .get()
            .uri { uriBuilder -> uriBuilder.path(MEMBER_BASE_URL).queryParam("name", name).build() }
            .retrieve()
            .bodyToMono(Member::class.java)

    fun findMembersByNameLike(nameLike: String): Flux<Member> =
        webClient
            .get()
            .uri { uriBuilder -> uriBuilder.path(MEMBER_BASE_URL).queryParam("nameLike", nameLike).build() }
            .retrieve()
            .bodyToFlux(Member::class.java)

    fun findBalanceByMemberId(memberId: Int): Mono<Balance> =
        webClient
            .get()
            .uri("$BALANCE_BASE_URL/{id}", memberId)
            .retrieve()
            .bodyToMono(Balance::class.java)

    fun findBalancesByMemberIds(memberIds: List<Int>): Flux<Balance> =
        webClient
            .get()
            .uri { uriBuilder -> uriBuilder.path(BALANCE_BASE_URL).queryParam("memberIds", memberIds).build() }
            .retrieve()
            .bodyToFlux(Balance::class.java)

    fun findBalancesByAmountIsBetween(minAmount: Long, maxAmount: Long): Flux<Balance> =
        webClient
            .get()
            .uri { uriBuilder -> uriBuilder.path(BALANCE_BASE_URL).queryParam("minAmount", minAmount).queryParam("maxAmount", maxAmount).build() }
            .retrieve()
            .bodyToFlux(Balance::class.java)

    fun doTransfer(request: CreateTransferRequest): Mono<Transfer> =
        webClient
            .post()
            .uri(TRANSFER_BASE_URL)
            .body(Mono.just(request), CreateTransferRequest::class.java)
            .retrieve()
            .bodyToMono(Transfer::class.java)

    fun findTransferByTransferId(transferId: Long): Mono<Transfer> =
        webClient
            .get()
            .uri("$TRANSFER_BASE_URL/{id}", transferId)
            .retrieve()
            .bodyToMono(Transfer::class.java)

    fun findTransfersByFromMemberId(fromMemberId: Int): Flux<Transfer> =
        webClient
            .get()
            .uri { uriBuilder -> uriBuilder.path(TRANSFER_BASE_URL).queryParam("fromMemberId", fromMemberId).build() }
            .retrieve()
            .bodyToFlux(Transfer::class.java)

    fun findTransfersByToMemberId(toMemberId: Int): Flux<Transfer> =
        webClient
            .get()
            .uri { uriBuilder -> uriBuilder.path(TRANSFER_BASE_URL).queryParam("toMemberId", toMemberId).build() }
            .retrieve()
            .bodyToFlux(Transfer::class.java)

}