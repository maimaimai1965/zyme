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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import ua.mai.zyme.r2dbcmysql.R2dbcMysqlApplicationTests;
import ua.mai.zyme.r2dbcmysql.config.AppTestConfig;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.util.TestUtil;

import java.util.List;

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
        // Setup
        Member member1 = tu.insertMember("member1Test");
        Member member2 = tu.insertMember("member2Test");

        Balance balance1 = tu.insertBalance(
                new Balance(member1.getMemberId(), 70L, TestUtil.now(), TestUtil.now()));
        Balance balance2 = tu.insertBalance(
                new Balance(member2.getMemberId(), 10L, TestUtil.now(), TestUtil.now()));

        // Execution
        Balance balanceOut1 = webTestClient.get()
                .uri("/api/balances/" + member1.getMemberId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().isOk()  // 200
                .expectBody(Balance.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(balanceOut1)
                .usingRecursiveComparison()
                .isEqualTo(balance1);
    }


    // ------------------------------------ findBalancesByMemberIds(memberIds) <- /api/balances?memberIds= -------------
    @Test
    public void findBalancesByMemberIds() {
        // Setup
        Member member1 = tu.insertMember("member1Test");
        Member member2 = tu.insertMember("member2Test");
        Member member3 = tu.insertMember("member3Test");

        Balance balance1 = tu.insertBalance(
                new Balance(member1.getMemberId(), 70L, TestUtil.now(), TestUtil.now()));
        Balance balance2 = tu.insertBalance(
                new Balance(member2.getMemberId(), 10L, TestUtil.now(), TestUtil.now()));
        Balance balance3 = tu.insertBalance(
                new Balance(member3.getMemberId(), 80L, TestUtil.now(), TestUtil.now()));

        List<Balance> listForCheck = List.of(balance1, balance3);

        // Execution
        List<Balance> listOut = webTestClient.get()
                .uri("/api/balances?memberIds=" + balance1.getMemberId() + "," + balance3.getMemberId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().isOk()  // 200
                .returnResult(Balance.class)
                .getResponseBody()
                .toStream().toList();

        Assertions.assertThat(listOut).containsExactlyInAnyOrderElementsOf(listForCheck);
    }


    // -------- findBalancesByAmountIsBetween(minAmount, maxAmount) <- /api/balances?minAmount= &maxAmount= ------------
    @Test
    public void findBalancesByAmountIsBetween() {
        // Setup
        Member member1 = tu.insertMember("member1Test");
        Member member2 = tu.insertMember("member2Test");
        Member member3 = tu.insertMember("member3Test");

        Balance balance1 = tu.insertBalance(
                new Balance(member1.getMemberId(), 70L, TestUtil.now(), TestUtil.now()));
        Balance balance2 = tu.insertBalance(
                new Balance(member2.getMemberId(), 10L, TestUtil.now(), TestUtil.now()));
        Balance balance3 = tu.insertBalance(
                new Balance(member3.getMemberId(), 80L, TestUtil.now(), TestUtil.now()));

        List<Balance> listForCheck = List.of(balance1, balance3);

        // Execution
        List<Balance> listOut = webTestClient.get()
                .uri("/api/balances?minAmount=70&maxAmount=80")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().isOk()  // 200
                .returnResult(Balance.class)
                .getResponseBody()
                .toStream().toList();

        Assertions.assertThat(listOut).containsExactlyInAnyOrderElementsOf(listForCheck);
    }

}
