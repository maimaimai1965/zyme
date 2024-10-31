package ua.mai.zyme.r2dbcmysql.webclient;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ua.mai.zyme.r2dbcmysql.R2dbcMysqlApplicationTests;
import ua.mai.zyme.r2dbcmysql.config.AppTestConfig;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.util.TestUtil;

import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@ContextConfiguration(classes = {R2dbcMysqlApplicationTests.class})
@EnableR2dbcRepositories(basePackages = {"ua.mai.zyme.r2dbcmysql.repository"})
@Import(AppTestConfig.class)
@Slf4j
@ActiveProfiles(profiles = "test")
public class R2dbsMysqlWebClientForBalanceControllerTests {

    @Autowired
    private R2dbsMysqlWebClient mysqlWebClient;

    @Autowired
    private ConnectionFactory connectionFactory;
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


    // ------------------------------------ findBalanceByMemberId(memberId) --------------------------------------------

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
        Balance balanceOut1 = mysqlWebClient.findBalanceByMemberId(member1.getMemberId()).block();
        // Assertion
        Assertions.assertThat(balanceOut1)
                .usingRecursiveComparison()
                .isEqualTo(balance1);
    }

    @Test
    public void findBalanceByMemberId_WhenBalanceNotFound() {
        // Execution
        Balance balanceOut = mysqlWebClient.findBalanceByMemberId(-1).block();
        // Assertion
        assertNull(balanceOut);
    }


    // ------------------------------------ findBalancesByMemberIds(memberIds) -----------------------------------------

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
        List<Balance> listOut =
                mysqlWebClient.findBalancesByMemberIds(listForCheck.stream().map(balance -> balance.getMemberId()).toList())
                              .toStream().toList();
        // Assertion
        Assertions.assertThat(listOut).containsExactlyInAnyOrderElementsOf(listForCheck);
    }

    @Test
    public void findBalancesByMemberIds_WhenBalancesNotFound() {
        // Execution
        List<Balance> listOut =
                mysqlWebClient.findBalancesByMemberIds(List.of(-1, -2))
                        .toStream().toList();
        // Assertion
        assertTrue(listOut.isEmpty());
    }


    // ------------------------------------ findBalancesByAmountIsBetween(minAmount, maxAmount) ------------------------

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
        List<Balance> listOut =
                mysqlWebClient.findBalancesByAmountIsBetween(70L, 80L)
                        .toStream().toList();
        // Assertion
        Assertions.assertThat(listOut).containsExactlyInAnyOrderElementsOf(listForCheck);
    }

}
