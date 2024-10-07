package ua.mai.zyme.r2dbcmysql;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.dto.CreateTransferRequest;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.exception.FaultInfo;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.repository.TransferRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@SpringBootTest
@AutoConfigureWebTestClient
@Slf4j
@ActiveProfiles(profiles = "test")
class TransferControllerTest {

    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private TransferRepository transferRepository;

    private TestUtil tu;

    @BeforeEach
    public void setup() {
        tu = new TestUtil();
        tu.setConnectionFactory(connectionFactory);
        tu.setMemberRepository(memberRepository);
        tu.setBalanceRepository(balanceRepository);
        tu.setTransferRepository(transferRepository);
    }

    @AfterEach
    public void cleanup() {
        // Teardown
        tu.deleteTransfersTestData();
        tu.deleteBalancesTestData();
        tu.deleteMembersTestData();
    }


    // ------------------------------------ doTransfer() <- /api/transfers ---------------------------------------------

    @Test
    public void doTransfer() throws InterruptedException {
        // Setup
        Member memberFrom = tu.insertMemberWithBalance("benTest", 40L, TestUtil.now());
        Member memberTo = tu.insertMemberWithBalance("annaTest", 70L, TestUtil.now());
        Thread.sleep(1000);  // Чтобы дата создания transfer отличалась от даты создания memberFrom и memberTo.
        CreateTransferRequest transferRequest = CreateTransferRequest.builder()
                .fromMemberId(memberFrom.getMemberId())
                .toMemberId(memberTo.getMemberId())
                .amount(20L)
                .build();

        // Execution
        Transfer transferOut = webTestClient
                .post()
                .uri("/api/transfers")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(transferRequest), CreateTransferRequest.class)
                .exchange()
        // Assertion
                .expectStatus()
                .isOk()
                .expectBody(Transfer.class)
                .returnResult().getResponseBody();

        assertNotNull(transferOut.getTransferId());
        Transfer transferDb = tu.findTransferByTransferId(transferOut.getTransferId());
        Assertions.assertThat(transferOut)
                .usingRecursiveComparison()
                .isEqualTo(transferDb);
    }

    @Test
    public void doTransfer_WhenToMemberBalanceNotExists() throws InterruptedException {
        // Setup
        Member memberFrom = tu.insertMemberWithBalance("benTest", 40L, TestUtil.now());
        Member memberTo = tu.insertMember("annaTest");
        Thread.sleep(1000);  // Чтобы дата создания transfer отличалась от даты создания memberFrom и memberTo.
        CreateTransferRequest transferRequest = CreateTransferRequest.builder()
                .fromMemberId(memberFrom.getMemberId())
                .toMemberId(memberTo.getMemberId())
                .amount(20L)
                .build();

        // Execution
        Transfer transferOut = webTestClient
                .post()
                .uri("/api/transfers")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(transferRequest), CreateTransferRequest.class)
                .exchange()
                // Assertion
                .expectStatus()
                .isOk()
                .expectBody(Transfer.class)
                .returnResult().getResponseBody();

        assertNotNull(transferOut.getTransferId());
        Transfer transferDb = tu.findTransferByTransferId(transferOut.getTransferId());
        Assertions.assertThat(transferOut)
                .usingRecursiveComparison()
                .isEqualTo(transferDb);

//        // Setup
//        Member memberFrom = tu.insertMemberWithBalance("benTest", 40L, TestUtil.now());
//        Member memberTo = tu.insertMember("annaTest");
//        Thread.sleep(1000);  // Чтобы отличались даты создания и изменения.
//        CreateTransferRequest transferRequest = CreateTransferRequest.builder()
//                .fromMemberId(memberFrom.getMemberId())
//                .toMemberId(memberTo.getMemberId())
//                .amount(20L)
//                .build();
//
//        // Execution
//        Transfer transferOut = webTestClient
//                .post()
//                .uri("/api/transfers")
//                .accept(MediaType.APPLICATION_JSON)
//                .body(Mono.just(transferRequest), CreateTransferRequest.class)
//                .exchange()
//        // Assertion
//                .expectStatus()
//                .isOk()
//                .expectBody(Transfer.class)
//                .returnResult().getResponseBody();
//
//        assertNotNull(transferOut.getTransferId());
//        Balance balanceTo_Db = tu.findBalanceByMemberId(memberTo.getMemberId());
//        assertEquals(memberTo.getMemberId(), balanceTo_Db.getMemberId());
//        assertEquals(0L + 40L, balanceTo_Db.getAmount().longValue());
//
//        Transfer transfer_Db = tu.findTransferByTransferId(transferOut.getTransferId());
//        Assertions.assertThat(transferOut)
//                .usingRecursiveComparison()
//                .isEqualTo(transfer_Db);
    }

    @Test
    public void doTransfer_() throws InterruptedException {
        // Setup
        Member memberFrom = tu.insertMember("benTest");
        Member memberTo = tu.insertMemberWithBalance("rikTest", 70L, TestUtil.now());
        Thread.sleep(1000);  // Чтобы дата создания transfer отличалась от даты создания memberFrom и memberTo.
        CreateTransferRequest transferRequest = CreateTransferRequest.builder()
                .fromMemberId(memberFrom.getMemberId())
                .toMemberId(memberTo.getMemberId())
                .amount(20L)
                .build();

        // Execution
        webTestClient
                .post()
                .uri("/api/transfers")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(transferRequest), CreateTransferRequest.class)
                .exchange()
        // Assertion
                .expectStatus().value(status ->
                        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), status.intValue()))  // 500
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(response.getResponseBody());
                    assertTrue(body.contains(AppFaultInfo.BALANCE_FOR_MEMBER_NOT_FOUND.code()));
                });
    }

}
