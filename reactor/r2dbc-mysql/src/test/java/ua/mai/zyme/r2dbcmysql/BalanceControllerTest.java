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
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.util.TestUtil;

import java.util.List;

import static org.hamcrest.Matchers.hasItems;


@SpringBootTest
@AutoConfigureWebTestClient
@Slf4j
@ActiveProfiles(profiles = "test")
class BalanceControllerTest {

    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BalanceRepository balanceRepository;

    private TestUtil tu;

    @BeforeEach
    public void setup() {
        tu = new TestUtil();
        tu.setConnectionFactory(connectionFactory);
        tu.setMemberRepository(memberRepository);
        tu.setBalanceRepository(balanceRepository);
    }

    @AfterEach
    public void cleanup() {
        // Teardown
        tu.deleteBalancesTestData();
        tu.deleteMembersTestData();
    }


    // ------------------------------------ findBalanceByMemberId(name) <- /api/balances/{memberId} --------------------

    @Test
    public void findBalanceByMemberId() {
        Member member1 = tu.insertMember("member1Test");
        Member member2 = tu.insertMember("member2Test");

        Balance balance1 = tu.insertBalance(Balance.builder()
                .memberId(member1.getMemberId())
                .amount(70L)
                .createdDate(TestUtil.now())
                .lastModifiedDate(TestUtil.now())
                .build());
        Balance balance2 = tu.insertBalance(Balance.builder()
                .memberId(member2.getMemberId())
                .amount(10L)
                .createdDate(TestUtil.now())
                .lastModifiedDate(TestUtil.now())
                .build());

        // Execution
        Balance balanceOut1 = webTestClient.get()
                .uri("/api/balances/" + member1.getMemberId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus()
                .isOk()
                .expectBody(Balance.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(balance1)
                .usingRecursiveComparison()
                .isEqualTo(balanceOut1);
    }


    // ------------------------------------ findBalancesByMemberIds(memberIds) <- /api/balances?memberIds= -------------

    @Test
    public void findBalancesByMemberIds() {
        Member member1 = tu.insertMember("member1Test");
        Member member2 = tu.insertMember("member2Test");
        Member member3 = tu.insertMember("member3Test");

        Balance balance1 = tu.insertBalance(Balance.builder()
                .memberId(member1.getMemberId())
                .amount(70L)
                .createdDate(TestUtil.now())
                .lastModifiedDate(TestUtil.now())
                .build());
        Balance balance2 = tu.insertBalance(Balance.builder()
                .memberId(member2.getMemberId())
                .amount(10L)
                .createdDate(TestUtil.now())
                .lastModifiedDate(TestUtil.now())
                .build());
        Balance balance3 = tu.insertBalance(Balance.builder()
                .memberId(member3.getMemberId())
                .amount(80L)
                .createdDate(TestUtil.now())
                .lastModifiedDate(TestUtil.now())
                .build());

        List<Balance> listForCheck = List.of(balance1, balance3);

        // Execution
        List<Balance> listOut = webTestClient.get()
                .uri("/api/balances?memberIds=" + balance1.getMemberId() + "," + balance3.getMemberId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .returnResult(Balance.class)
                .getResponseBody()
                .toStream().toList();

        Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listOut);
    }


    // -------- findBalancesByAmountIsBetween(minAmount, maxAmount) <- /api/balances?minAmount= &maxAmount= ------------

    @Test
    public void findBalancesByAmountIsBetween() {
        Member member1 = tu.insertMember("member1Test");
        Member member2 = tu.insertMember("member2Test");
        Member member3 = tu.insertMember("member3Test");

        Balance balance1 = tu.insertBalance(Balance.builder()
                .memberId(member1.getMemberId())
                .amount(70L)
                .createdDate(TestUtil.now())
                .lastModifiedDate(TestUtil.now())
                .build());
        Balance balance2 = tu.insertBalance(Balance.builder()
                .memberId(member2.getMemberId())
                .amount(10L)
                .createdDate(TestUtil.now())
                .lastModifiedDate(TestUtil.now())
                .build());
        Balance balance3 = tu.insertBalance(Balance.builder()
                .memberId(member3.getMemberId())
                .amount(80L)
                .createdDate(TestUtil.now())
                .lastModifiedDate(TestUtil.now())
                .build());

        List<Balance> listForCheck = List.of(balance1, balance3);

        // Execution
        List<Balance> listOut = webTestClient.get()
                .uri("/api/balances?minAmount=70&maxAmount=80")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // Assertion
                .returnResult(Balance.class)
                .getResponseBody()
                .toStream().toList();

        Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listOut);
    }

}
