package ua.mai.zyme.r2dbcmysql.repository;

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
import reactor.test.StepVerifier;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.util.TestUtil;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@SpringBootTest
@ContextConfiguration(classes = {R2dbcMysqlApplicationTests.class})
@EnableR2dbcRepositories(basePackages = {"ua.mai.zyme.r2dbcmysql.repository"})
@Import(AppTestConfig.class)
@Slf4j
@ActiveProfiles(profiles = "test")
class BalanсeRepositoryTest {

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


    @Test
    public void insert() {
        // Setup
        Member memberIn = tu.insertMember("monaTest");
        Balance balanceIn = Balance.builder()
                .memberId(memberIn.getMemberId())
                .amount(30L)
                .createdDate(TestUtil.now())
                .lastModifiedDate(TestUtil.now())
                .build();

        // Execution
        StepVerifier.create(
                balanceRepository.insert(balanceIn.getMemberId(), balanceIn.getAmount(), balanceIn.getCreatedDate(), balanceIn.getLastModifiedDate()))
        // Assertion
                    .verifyComplete();

        Balance balance_Db = tu.findBalanceByMemberId(memberIn.getMemberId());
        Assertions.assertThat(balanceIn)
                .usingRecursiveComparison()
                .isEqualTo(balance_Db);
    }

    @Test
    public void saveAsUpdate() {
        // Setup
        LocalDateTime now =TestUtil.now();
        Member member = tu.insertMemberWithBalance("mikeTest", 40L, now);
        Balance balanceIn = tu.findBalanceByMemberId(member.getMemberId());
        balanceIn.setAmount(80L);
        balanceIn.setLastModifiedDate(now.plus(2, ChronoUnit.MINUTES));
        List<Balance> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                balanceRepository.save(balanceIn))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Balance balance_Db = tu.findBalanceByMemberId(member.getMemberId());
        Assertions.assertThat(balanceIn)
                .usingRecursiveComparison()
                .isEqualTo(balance_Db);
    }

//  @Test
//  public void saveAllAsInsert() {
//    // Setup
//    List<Member> listIn = new ArrayList<>(List.of(
//            TestUtil.newMember("veraTest"),
//            TestUtil.newMember("benTest"),
//            TestUtil.newMember("tomTest")
//    ));
//
//    // Execution
//    List<Member> listOut = memberRepository.saveAll(listIn).toStream().toList();
//
//    // Assertion
//    assertTrue(listOut.size() == 2);
//    listOut.forEach(member -> {
//        assertNotNull(member.getMemberId());
//    });
//    List<Member> listDb = List.of(tu.findMemberById(listOut.get(0).getMemberId()),
//                                  tu.findMemberById(listOut.get(1).getMemberId()));
//    Assertions.assertThat(listIn).containsExactlyInAnyOrderElementsOf(listOut);
//  }

    @Test
    public void deleteById() {
        // Setup
        Member memberIn = tu.insertMember("tomTest");
        Balance balanceIn = tu.insertBalance(Balance.builder()
                .memberId(memberIn.getMemberId())
                .amount(70L)
                .createdDate(TestUtil.now())
                .lastModifiedDate(TestUtil.now())
                .build()
        );
        tu.findBalanceByMemberId(balanceIn.getMemberId());

        // Execution
        StepVerifier.create(
                balanceRepository.deleteById(balanceIn.getMemberId()))
        // Assertion
                    .verifyComplete();

        Balance balance_Db = tu.findBalanceByMemberId(balanceIn.getMemberId());
        assertNull(balance_Db);
    }

    @Test
    public void deleteAllById() {
        // Setup
        LocalDateTime now = TestUtil.now();
        Member member1 = tu.insertMemberWithBalance("member1Test", 70L, now);
        Member member2 = tu.insertMemberWithBalance("member2Test", 10L, now);
        Member member3 = tu.insertMemberWithBalance("member3Test", 80L, now);
        List<Integer> listForCheck = List.of(member1.getMemberId(), member3.getMemberId());

        // Execution
        StepVerifier.create(
                balanceRepository.deleteAllById(listForCheck))
        // Assertion
                    .verifyComplete();

        assertNull(tu.findBalanceByMemberId(member1.getMemberId()));
        assertNotNull(tu.findBalanceByMemberId(member2.getMemberId()));
        assertNull(tu.findBalanceByMemberId(member3.getMemberId()));
    }


    @Test
    public void findByMemberId() {
        // Setup
        LocalDateTime now = TestUtil.now();
        Member memberIn = tu.insertMemberWithBalance("rikTest", 60L, now);
        Balance balanceIn = tu.findBalanceByMemberId(memberIn.getMemberId());
        Member memberIn2 = tu.insertMemberWithBalance("sedTest", 80L, now);
        List<Balance> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                balanceRepository.findByMemberId(memberIn.getMemberId()))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Balance balanceOut = listResult.get(0);
        Assertions.assertThat(balanceIn)
                  .usingRecursiveComparison()
                  .isEqualTo(balanceOut);
    }

    @Test
    public void findByMemberId_WhenNotExists() {
        // Setup
        Member memberIn = tu.insertMemberWithBalance("rikTest", 60L, TestUtil.now());
        Integer noExistsId = -1;

        // Execution
        StepVerifier.create(
                         balanceRepository.findByMemberId(noExistsId))
        // Assertion
                    .verifyComplete(); // Проверяет получение Mono с пустым значением.
    }

    @Test
    public void findAllById() throws InterruptedException {
        // Setup
        LocalDateTime now =TestUtil.now();
        Member member1 = tu.insertMemberWithBalance("member1Test", 70L, now);
        Member member2 = tu.insertMemberWithBalance("member2Test", 10L, now);
        Member member3 = tu.insertMemberWithBalance("member3Test", 80L, now);

        Balance balance1 = tu.findBalanceByMemberId(member1.getMemberId());
        Balance balance2 = tu.findBalanceByMemberId(member2.getMemberId());
        Balance balance3 = tu.findBalanceByMemberId(member3.getMemberId());

        List<Balance> listForCheck = List.of(balance1, balance3);
        List<Integer> listIdForCheck = List.of(balance1.getMemberId(), balance3.getMemberId());
        List<Balance> listResult = new ArrayList<>();

        // Execution
        StepVerifier.create(
                balanceRepository.findAllById(listIdForCheck))
        // Assertion
                    .consumeNextWith(listResult::add)
                    .consumeNextWith(listResult::add)
                    .verifyComplete();

        Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listResult);
    }


    @Test
    public void findByAmountIsBetween() throws InterruptedException {
        // Setup
        LocalDateTime now =TestUtil.now();
        Member member1 = tu.insertMemberWithBalance("member1Test", 70L, now);
        Member member2 = tu.insertMemberWithBalance("member2Test", 10L, now);
        Member member3 = tu.insertMemberWithBalance("member3Test", 80L, now);
        Member member4 = tu.insertMemberWithBalance("member4Test", 81L, now);

        Balance balance1 = tu.findBalanceByMemberId(member1.getMemberId());
        Balance balance2 = tu.findBalanceByMemberId(member2.getMemberId());
        Balance balance3 = tu.findBalanceByMemberId(member3.getMemberId());
        Balance balance4 = tu.findBalanceByMemberId(member4.getMemberId());

        List<Balance> listForCheck = List.of(balance1, balance3);
        List<Balance> listResult = new ArrayList<>();

        // Execution
        StepVerifier.create(
                         balanceRepository.findByAmountIsBetween(70L, 80L))
        // Assertion
                    .consumeNextWith(listResult::add)
                    .consumeNextWith(listResult::add)
                    .verifyComplete();

        Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listResult);
    }

}
