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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.dto.CreateTransferRequest;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.repository.TransferRepository;

import java.util.List;

import static org.junit.Assert.assertNotNull;


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


    @Test
    public void doTransfer() throws InterruptedException {
        // Setup
        Member memberFrom = tu.insertMemberWithBalance("benTest", 40L, TestUtil.now());
        Member memberTo = tu.insertMemberWithBalance("annaTest", 70L, TestUtil.now());
        Thread.sleep(1000);  // Чтобы отличались даты создания и изменения.
        CreateTransferRequest transferRequest = CreateTransferRequest.builder()
                .fromMemberId(memberFrom.getMemberId())
                .toMemberId(memberTo.getMemberId())
                .amount(20L)
                .build();

        // Execution
        Transfer transferOut = webTestClient.post()
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

}
