package ua.mai.zyme.r2dbcmysql.controller;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import ua.mai.zyme.r2dbcmysql.R2dbcMysqlApplicationTests;
import ua.mai.zyme.r2dbcmysql.config.AppTestConfig;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.dto.CreateTransferRequest;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.exception.FaultInfo;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.repository.TransferRepository;
import ua.mai.zyme.r2dbcmysql.util.TestUtil;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // Запуск всего контекста на
@ContextConfiguration(classes = {R2dbcMysqlApplicationTests.class})
@EnableR2dbcRepositories(basePackages = {"ua.mai.zyme.r2dbcmysql.repository"})
@ComponentScan(basePackages = {
        "ua.mai.zyme.r2dbcmysql.service",
        "ua.mai.zyme.r2dbcmysql.controller",
        "ua.mai.zyme.r2dbcmysql.exception",
})
@Import(AppTestConfig.class)
@EnableAutoConfiguration
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
        Transfer transfer_Db = tu.findTransferByTransferId(transferOut.getTransferId());
        Assertions.assertThat(transferOut)
                .usingRecursiveComparison()
                .isEqualTo(transfer_Db);
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
        Transfer transfer_Db = tu.findTransferByTransferId(transferOut.getTransferId());
        Assertions.assertThat(transferOut)
                .usingRecursiveComparison()
                .isEqualTo(transfer_Db);
    }

    @Test
    public void doTransfer_Fault_WhenMemberFromNotExists() throws InterruptedException {
        // Setup
        Integer memberFromIdNotExists = -1;
        Integer memberTo = -2;
        CreateTransferRequest transferRequest = CreateTransferRequest.builder()
                .fromMemberId(memberFromIdNotExists)
                .toMemberId(memberTo)
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
                     assertTrue(body.contains("(memberId=" + memberFromIdNotExists.toString()));
                 });
    }

    @Test
    public void doTransfer_Fault_WhenBalanceFromNotExists() throws InterruptedException {
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
                    assertTrue(body.contains(memberFrom.getMemberId().toString()));
                });
    }

    @Test
    public void doTransfer_Fault_WhenZeroTransferAmount() throws InterruptedException {
        // Setup
        Member memberFrom = tu.insertMemberWithBalance("stanislavTest", 100L, TestUtil.now());
        Member memberTo = tu.insertMemberWithBalance("rikTest", 70L, TestUtil.now());
        Thread.sleep(1000);  // Чтобы дата создания transfer отличалась от даты создания memberFrom и memberTo.
        CreateTransferRequest transferRequest = CreateTransferRequest.builder()
                .fromMemberId(memberFrom.getMemberId())
                .toMemberId(memberTo.getMemberId())
                .amount(0L)
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
                    assertTrue(body.contains(AppFaultInfo.TRANSFER_AMOUNT_MUST_BE_POSITIVE.code()));
                });
    }


    // ------------------------------------ findTransfer(transferId) <- /api/transfers/{transferId} --------------------

    @Test
    public void findTransfer() throws InterruptedException {
        // Setup
        Member memberFrom = tu.insertMemberWithBalance("benTest", 40L, TestUtil.now());
        Member memberTo = tu.insertMemberWithBalance("annaTest", 70L, TestUtil.now());
        Thread.sleep(1000);  // Чтобы дата создания transfer отличалась от даты создания memberFrom и memberTo.
        Transfer transfer1 = tu.insertTransfer(memberFrom.getMemberId(), memberTo.getMemberId(), 20L);
        Transfer transfer2 = tu.insertTransfer(memberFrom.getMemberId(), memberTo.getMemberId(), 10L);

        // Execution
        Transfer transferOut = webTestClient
                .get()
                .uri("/api/transfers/" + transfer1.getTransferId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // Assertion
                .expectStatus()
                .isOk()
                .expectBody(Transfer.class)
                .returnResult().getResponseBody();

        assertNotNull(transferOut.getTransferId());
        Transfer transfer_Db = tu.findTransferByTransferId(transferOut.getTransferId());
        Assertions.assertThat(transferOut)
                .usingRecursiveComparison()
                .isEqualTo(transfer_Db);
    }

    @Test
    public void findTransfer_WhenNotExists() throws InterruptedException {
        // Setup
        Long transferIdNotExists = -1L;

        // Execution
        Transfer transferOut = webTestClient
                .get()
                .uri("/api/transfers/" + transferIdNotExists)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // Assertion
                .expectStatus()
                .isOk()
                .expectBody(Transfer.class)
                .returnResult().getResponseBody();

        assertNull(transferOut);
    }

    @Test
    public void findTransfer_Fault_WhenBadTransferId() throws InterruptedException {
        // Setup
        String badTransferId = "2W";

        // Execution
        webTestClient
                .get()
                .uri("/api/transfers/" + badTransferId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().value(status -> assertEquals("400", status.toString()))
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(response.getResponseBody());
                    assertTrue(body.contains(FaultInfo.UNEXPECTED_ERROR_CODE));
                });
    }


    //------------ findTransfersByFromMemberId(fromMemberId) <- /api/transfers/?fromMemberId= --------------------------

    @Test
    public void findTransfersByFromMemberId() throws InterruptedException {
        // Setup
        Member member1 = tu.insertMemberWithBalance("benTest", 50L, TestUtil.now());
        Member member2 = tu.insertMemberWithBalance("annaTest", 70L, TestUtil.now());
        Member member3 = tu.insertMemberWithBalance("rikTest", 70L, TestUtil.now());
        Transfer transfer1_2 = tu.insertTransfer(member1.getMemberId(), member2.getMemberId(), 20L);
        Thread.sleep(1000);
        Transfer transfer1_3 = tu.insertTransfer(member1.getMemberId(), member3.getMemberId(), 10L);
        Thread.sleep(1000);
        Transfer transfer2_1 = tu.insertTransfer(member2.getMemberId(), member1.getMemberId(), 15L);

        List<Transfer> transfersIn = List.of(transfer1_2, transfer1_3);

        // Execution
        List<Transfer> transfersOut = webTestClient
                .get()
                .uri("/api/transfers?fromMemberId=" + member1.getMemberId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // Assertion
                .returnResult(Transfer.class)
                .getResponseBody()
                .toStream().toList();

        Assertions.assertThat(transfersOut)
                .usingRecursiveComparison()
                .isEqualTo(transfersIn);
    }


    //------------ findTransfersByToMemberId(toMemberId) <- /api/transfers/?toMemberId= --------------------------------

    @Test
    public void findTransfersByToMemberId() throws InterruptedException {
        // Setup
        Member member1 = tu.insertMemberWithBalance("benTest", 50L, TestUtil.now());
        Member member2 = tu.insertMemberWithBalance("annaTest", 70L, TestUtil.now());
        Member member3 = tu.insertMemberWithBalance("rikTest", 70L, TestUtil.now());
        Transfer transfer1_2 = tu.insertTransfer(member1.getMemberId(), member2.getMemberId(), 20L);
        Thread.sleep(1000);
        Transfer transfer1_3 = tu.insertTransfer(member1.getMemberId(), member3.getMemberId(), 10L);
        Thread.sleep(1000);
        Transfer transfer2_3 = tu.insertTransfer(member2.getMemberId(), member3.getMemberId(), 15L);

        List<Transfer> transfersIn = List.of(transfer1_3, transfer2_3);

        // Execution
        List<Transfer> transfersOut = webTestClient
                .get()
                .uri("/api/transfers?toMemberId=" + member3.getMemberId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // Assertion
                .returnResult(Transfer.class)
                .getResponseBody()
                .toStream().toList();

        Assertions.assertThat(transfersOut)
                .usingRecursiveComparison()
                .isEqualTo(transfersIn);
    }

}
