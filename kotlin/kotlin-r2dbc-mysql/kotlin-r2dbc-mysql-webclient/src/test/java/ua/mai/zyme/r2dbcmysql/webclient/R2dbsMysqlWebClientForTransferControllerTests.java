package ua.mai.zyme.r2dbcmysql.webclient;

import ua.mai.zyme.r2dbcmysql.webclient.exception.AppClientError;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
import ua.mai.zyme.r2dbcmysql.dto.CreateTransferRequest;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.repository.TransferRepository;
import ua.mai.zyme.r2dbcmysql.util.TestUtil;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@ContextConfiguration(classes = {R2dbcMysqlApplicationTests.class})
@EnableR2dbcRepositories(basePackages = {"ua.mai.zyme.r2dbcmysql.repository"})
@Import(AppTestConfig.class)
@Slf4j
@ActiveProfiles(profiles = "test")
public class R2dbsMysqlWebClientForTransferControllerTests {

    @Autowired
    private R2dbsMysqlWebClient mysqlWebClient;

    @Autowired
    private ConnectionFactory connectionFactory;
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


    // ------------------------------------ doTransfer(transferRequest) ------------------------------------------------

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
        Transfer transferOut = mysqlWebClient.doTransfer(transferRequest).block();
        // Assertion
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
        Transfer transferOut = mysqlWebClient.doTransfer(transferRequest).block();
        // Assertion
        assertNotNull(transferOut.getTransferId());
        Transfer transfer_Db = tu.findTransferByTransferId(transferOut.getTransferId());
        Assertions.assertThat(transferOut)
                .usingRecursiveComparison()
                .isEqualTo(transfer_Db);
    }

    @Test
    public void doTransfer_ERR303_BalanceFromNotEnough() throws InterruptedException {
        // Setup
        Member memberFrom = tu.insertMemberWithBalance("benTest", 40L, TestUtil.now());
        Member memberTo = tu.insertMemberWithBalance("annaTest", 70L, TestUtil.now());
        CreateTransferRequest transferRequest = CreateTransferRequest.builder()
                .fromMemberId(memberFrom.getMemberId())
                .toMemberId(memberTo.getMemberId())
                .amount(50L)
                .build();

        AppClientError error = assertThrows(AppClientError.class, () -> {
        // Execution
            mysqlWebClient.doTransfer(transferRequest).block();
        });
        // Assertion
        assertEquals("500", error.getClientFaultInfo().getStatus());
        assertEquals(AppFaultInfo.BALANCE_AMOUNT_NOT_ENOUGH.code(), error.getClientFaultInfo().getErrorCd());
//        assertTrue(error.getClientFaultInfo().getErrorMsg().contains("(memberId=" + memberFrom.getMemberId()));
    }

    @Test
    public void doTransfer_ERR300_WhenMemberFromNotExists() throws InterruptedException {
        // Setup
        Integer memberFromIdNotExists = -1;
        Integer memberTo = -2;
        CreateTransferRequest transferRequest = CreateTransferRequest.builder()
                .fromMemberId(memberFromIdNotExists)
                .toMemberId(memberTo)
                .amount(20L)
                .build();

        AppClientError error = assertThrows(AppClientError.class, () -> {
        // Execution
            mysqlWebClient.doTransfer(transferRequest).block();
        });
        // Assertion
        assertEquals("500", error.getClientFaultInfo().getStatus());
        assertEquals(AppFaultInfo.BALANCE_FOR_MEMBER_NOT_FOUND.code(), error.getClientFaultInfo().getErrorCd());
        assertTrue(error.getClientFaultInfo().getErrorMsg().contains("(memberId=" + memberFromIdNotExists.toString()));
    }

    @Test
    public void doTransfer_ERR300_WhenBalanceFromNotExists() throws InterruptedException {
        // Setup
        Member memberFrom = tu.insertMember("benTest");
        Member memberTo = tu.insertMemberWithBalance("rikTest", 70L, TestUtil.now());
        Thread.sleep(1000);  // Чтобы дата создания transfer отличалась от даты создания memberFrom и memberTo.
        CreateTransferRequest transferRequest = CreateTransferRequest.builder()
                .fromMemberId(memberFrom.getMemberId())
                .toMemberId(memberTo.getMemberId())
                .amount(20L)
                .build();

        AppClientError error = assertThrows(AppClientError.class, () -> {
        // Execution
            mysqlWebClient.doTransfer(transferRequest).block();
        });
        // Assertion
        assertEquals("500", error.getClientFaultInfo().getStatus());
        assertEquals(AppFaultInfo.BALANCE_FOR_MEMBER_NOT_FOUND.code(), error.getClientFaultInfo().getErrorCd());
//        assertTrue(error.getClientFaultInfo().getErrorMsg().contains("(memberId=" + memberFrom.getMemberId().toString()));
    }

    @Test
    public void doTransfer_ERR402_WhenZeroTransferAmount() throws InterruptedException {
        // Setup
        Member memberFrom = tu.insertMemberWithBalance("stanislavTest", 100L, TestUtil.now());
        Member memberTo = tu.insertMemberWithBalance("rikTest", 70L, TestUtil.now());
        Thread.sleep(1000);  // Чтобы дата создания transfer отличалась от даты создания memberFrom и memberTo.
        CreateTransferRequest transferRequest = CreateTransferRequest.builder()
                .fromMemberId(memberFrom.getMemberId())
                .toMemberId(memberTo.getMemberId())
                .amount(0L)
                .build();

        AppClientError error = assertThrows(AppClientError.class, () -> {
        // Execution
            mysqlWebClient.doTransfer(transferRequest).block();
        });
        // Assertion
        assertEquals("500", error.getClientFaultInfo().getStatus());
        assertEquals(AppFaultInfo.TRANSFER_AMOUNT_MUST_BE_POSITIVE.code(), error.getClientFaultInfo().getErrorCd());
    }


    // ------------------------------------ findTransfer(transferId) ---------------------------------------------------

    @Test
    public void findTransfer() throws InterruptedException {
        // Setup
        Member memberFrom = tu.insertMemberWithBalance("benTest", 40L, TestUtil.now());
        Member memberTo = tu.insertMemberWithBalance("annaTest", 70L, TestUtil.now());
        Thread.sleep(1000);  // Чтобы дата создания transfer отличалась от даты создания memberFrom и memberTo.
        Transfer transfer1 = tu.insertTransfer(memberFrom.getMemberId(), memberTo.getMemberId(), 20L);
        Transfer transfer2 = tu.insertTransfer(memberFrom.getMemberId(), memberTo.getMemberId(), 10L);

        // Execution
        Transfer transferOut = mysqlWebClient.findTransferByTransferId(transfer1.getTransferId()).block();
        // Assertion
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
        Transfer transferOut = mysqlWebClient.findTransferByTransferId(transferIdNotExists).block();
        // Assertion
        assertNull(transferOut);
    }


    // ------------------------------------ findTransfersByFromMemberId(fromMemberId) ----------------------------------

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
        List<Transfer> transfersOut = mysqlWebClient.findTransfersByFromMemberId(member1.getMemberId()).toStream().toList();
        // Assertion
        Assertions.assertThat(transfersOut)
                .usingRecursiveComparison()
                .isEqualTo(transfersIn);
    }


    // ------------------------------------ findTransfersByToMemberId(toMemberId) --------------------------------------

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
        List<Transfer> transfersOut = mysqlWebClient.findTransfersByToMemberId(member3.getMemberId()).toStream().toList();
        // Assertion
        Assertions.assertThat(transfersOut)
                .usingRecursiveComparison()
                .isEqualTo(transfersIn);
    }

}
