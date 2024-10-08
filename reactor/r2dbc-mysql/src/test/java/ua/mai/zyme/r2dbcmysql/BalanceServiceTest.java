package ua.mai.zyme.r2dbcmysql;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.test.StepVerifier;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.exception.FaultException;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.service.BalanceService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@SpringJUnitConfig
@Slf4j
@ActiveProfiles(profiles = "test")
class BalanceServiceTest {

    @Autowired
    ConnectionFactory connectionFactory;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private BalanceService balanceService;

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
    public void insertBalance() throws InterruptedException {
        // Setup
        Member memberIn = tu.insertMember("monaTest");
        Balance balanceIn = Balance.builder()
                .memberId(memberIn.getMemberId())
                .amount(30L)
                .createdDate(TestUtil.now())
                .lastModifiedDate(TestUtil.now())
                .build();
        List<Balance> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                balanceService.insertBalance(balanceIn.getMemberId(), balanceIn.getAmount(), balanceIn.getCreatedDate(), balanceIn.getLastModifiedDate()))
       // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Balance balanceOut = listResult.get(0);
        Assertions.assertThat(balanceOut)
                .usingRecursiveComparison()
                .isEqualTo(balanceIn);
        Balance balance_Db = tu.findBalanceByMemberId(memberIn.getMemberId());
        Assertions.assertThat(balance_Db)
                .usingRecursiveComparison()
                .isEqualTo(balanceIn);
    }

    @Test
    public void insertBalance_Fault_WhenNegativeAmount() throws InterruptedException {
        // Setup
        Member memberIn = tu.insertMember("monaTest");
        Balance balanceIn = Balance.builder()
                .memberId(memberIn.getMemberId())
                .amount(-50L)
                .createdDate(TestUtil.now())
                .lastModifiedDate(TestUtil.now())
                .build();
        List<Balance> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                balanceService.insertBalance(balanceIn.getMemberId(), balanceIn.getAmount(), balanceIn.getCreatedDate(), balanceIn.getLastModifiedDate()))
        // Assertion
                    .consumeErrorWith(error -> {
                         assertThat(error).isInstanceOf(FaultException.class);
                         FaultException fault = (FaultException) error;
                         assertThat(fault.getCode()).isEqualTo(AppFaultInfo.BALANCE_AMOUNT_CANNOT_BE_NEGATIVE.code());
                })
                .verify(); // Проверяет получение Mono с пустым значением.
    }

    @Test
    public void insertBalance_Fault_WhenMemberNotExists() throws InterruptedException {
        // Setup
        Integer notExistsMemberId = -1;
        Balance balanceIn = Balance.builder()
                .memberId(notExistsMemberId)
                .amount(30L)
                .createdDate(TestUtil.now())
                .lastModifiedDate(TestUtil.now())
                .build();

        // Execution
        StepVerifier.create(
                balanceService.insertBalance(balanceIn.getMemberId(), balanceIn.getAmount(), balanceIn.getCreatedDate(), balanceIn.getLastModifiedDate()))
        // Assertion
                    .consumeErrorWith(error -> {
                         assertThat(error).isInstanceOf(FaultException.class);
                         FaultException fault = (FaultException) error;
                         assertThat(fault.getCode()).isEqualTo(AppFaultInfo.BALANCE_NOT_CREATED.code());
                         assertThat(fault.getErrorParameters().get(0)).isEqualTo(balanceIn.getMemberId());
                         assertThat(fault.getErrorParameters().get(1)).isEqualTo(balanceIn.getAmount());
                     })
                    .verify();
    }

    @Test
    public void insertZeroBalance() throws InterruptedException {
        // Setup
        LocalDateTime now = TestUtil.now();
        Member memberIn = tu.insertMember("monaTest");
        Balance balanceIn = Balance.builder()
                .memberId(memberIn.getMemberId())
                .amount(0L)
                .createdDate(now)
                .lastModifiedDate(now)
                .build();
        List<Balance> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                balanceService.insertZeroBalance(balanceIn.getMemberId(), now))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Balance balanceOut = listResult.get(0);
        Assertions.assertThat(balanceOut)
                .usingRecursiveComparison()
                .isEqualTo(balanceIn);
        Balance balance_Db = tu.findBalanceByMemberId(memberIn.getMemberId());
        Assertions.assertThat(balance_Db)
                .usingRecursiveComparison()
                .isEqualTo(balanceIn);
    }

    @Test
    public void findBalanceByMemberId() {
        // Setup
        LocalDateTime now = TestUtil.now();
        Member memberIn = tu.insertMemberWithBalance("rikTest", 60L, now);
        Balance balanceIn = tu.findBalanceByMemberId(memberIn.getMemberId());
        Member memberIn2 = tu.insertMemberWithBalance("sedTest", 80L, now);
        List<Balance> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                balanceService.findBalanceByMemberId(memberIn.getMemberId()))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Balance balanceOut = listResult.get(0);
        Assertions.assertThat(balanceOut)
                .usingRecursiveComparison()
                .isEqualTo(balanceIn);
    }

    @Test
    public void findBalanceByMemberId_WhenBalanceNotExists() {
        // Setup
        Member memberIn = tu.insertMemberWithBalance("rikTest", 60L, TestUtil.now());
        Integer noExistsId = -1;

        // Execution
        StepVerifier.create(
                balanceService.findBalanceByMemberId(noExistsId))
        // Assertion
                    .verifyComplete();  // Проверяет получение Mono с пустым значением.
    }

    @Test
    public void findBalanceByMemberIdWithFaultWhenBalanceNotExists() {
        // Setup
        LocalDateTime now = TestUtil.now();
        Member memberIn = tu.insertMemberWithBalance("rikTest", 60L, now);
        Balance balanceIn = tu.findBalanceByMemberId(memberIn.getMemberId());
        Member memberIn2 = tu.insertMemberWithBalance("sedTest", 80L, now);
        List<Balance> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                balanceService.findBalanceByMemberIdWithFaultWhenBalanceNotExists(memberIn.getMemberId()))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Balance balanceOut = listResult.get(0);
        Assertions.assertThat(balanceOut)
                .usingRecursiveComparison()
                .isEqualTo(balanceIn);
    }

    @Test
    public void findBalanceByMemberIdWithFaultWhenBalanceNotExists_Fault_WhenBalanceNotExists() {
        // Setup
        Member memberIn = tu.insertMember("rikTest");

        // Execution
        StepVerifier.create(
                balanceService.findBalanceByMemberIdWithFaultWhenBalanceNotExists(memberIn.getMemberId()))
        // Assertion
                    .consumeErrorWith(error -> {
                         assertThat(error).isInstanceOf(FaultException.class);
                         FaultException fault = (FaultException) error;
                         assertThat(fault.getCode()).isEqualTo(AppFaultInfo.BALANCE_FOR_MEMBER_NOT_FOUND.code());
                         assertThat(fault.getErrorParameters().get(0)).isEqualTo(memberIn.getMemberId());
                     })
                    .verify(); // Проверяет получение Mono с пустым значением.
    }

    @Test
    public void findBalanceByMemberIdWithCreateZeroBalanceIfNotExists_WhenBalanceNotExists() {
        // Setup
        LocalDateTime now = TestUtil.now();
        Member memberIn = tu.insertMember("madeTest");
        Balance expectedBalance = Balance.builder()
                .memberId(memberIn.getMemberId())
                .amount(0L)
                .createdDate(now)
                .lastModifiedDate(now)
                .build();
        List<Balance> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                balanceService.findBalanceByMemberIdWithCreateZeroBalanceIfNotExists(memberIn.getMemberId(), now))
        // Assertion
                .consumeNextWith(result -> listResult.add(result))
                .verifyComplete(); // Проверяет получение Mono с пустым значением.

        Balance balanceOut = listResult.get(0);
        Assertions.assertThat(balanceOut)
                .usingRecursiveComparison()
                .isEqualTo(expectedBalance);
        Balance balance_Db = tu.findBalanceByMemberId(memberIn.getMemberId());
        Assertions.assertThat(balance_Db)
                .usingRecursiveComparison()
                .isEqualTo(expectedBalance);
    }


}
