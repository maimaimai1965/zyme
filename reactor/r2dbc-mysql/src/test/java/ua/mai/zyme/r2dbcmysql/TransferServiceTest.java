package ua.mai.zyme.r2dbcmysql;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
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
import ua.mai.zyme.r2dbcmysql.entity.Transfer;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.exception.ServiceFault;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.repository.TransferRepository;
import ua.mai.zyme.r2dbcmysql.service.TransferService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@SpringJUnitConfig
@Slf4j
@ActiveProfiles(profiles = "test")
class TransferServiceTest {

    @Autowired
    ConnectionFactory connectionFactory;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private TransferService transferService;

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
    public void doTransferWhenBalancesExist() throws InterruptedException {
        // Setup
        Member memberFrom = tu.insertMemberWithBalance("benTest", 40L, TestUtil.now());
        Member memberTo = tu.insertMemberWithBalance("annaTest", 70L, TestUtil.now());
        Thread.sleep(1000);  // Чтобы отличались даты создания и изменения.
        List<Transfer> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                   transferService.doTransferWhenBalancesExist(memberFrom.getMemberId(), memberTo.getMemberId(), 20L, TestUtil.now())
               )
              .consumeNextWith(result -> listResult.add(result))
              .verifyComplete();

        // Assertion
        Transfer transferOut = listResult.get(0);
        Assert.assertNotNull(transferOut.getTransferId());
        Balance balanceFrom_Db = tu.findBalanceByMemberId(memberFrom.getMemberId());
        Balance balanceTo_Db = tu.findBalanceByMemberId(memberTo.getMemberId());
        Transfer transfer_Db = tu.findTransferByTransferId(transferOut.getTransferId());
        Assert.assertEquals(20L, transfer_Db.getAmount().longValue());

        Assert.assertEquals(40L - 20L, balanceFrom_Db.getAmount().longValue());
        Assert.assertEquals(transfer_Db.getCreatedDate(), balanceFrom_Db.getLastModifiedDate());
        Assert.assertEquals(70L + 20L, balanceTo_Db.getAmount().longValue());
        Assert.assertEquals(transfer_Db.getCreatedDate(), balanceTo_Db.getLastModifiedDate());
    }

    @Test
    public void doTransferWhenBalancesExist_Fault_BalanceAmountNotEnough() throws InterruptedException {
        // Setup
        Member memberFrom = tu.insertMemberWithBalance("benTest", 40L, TestUtil.now());
        Member memberTo = tu.insertMemberWithBalance("annaTest", 70L, TestUtil.now());
        Thread.sleep(1000);  // Чтобы отличались даты создания и изменения.
        List<Transfer> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                        transferService.doTransferWhenBalancesExist(memberFrom.getMemberId(), memberTo.getMemberId(), 50L, TestUtil.now())
                )
                // Assertion
                .consumeErrorWith(error -> {
                    assertThat(error).isInstanceOf(ServiceFault.class);
                    ServiceFault fault = (ServiceFault) error;
                    assertThat(fault.getCode()).isEqualTo(AppFaultInfo.BALANCE_AMOUNT_NOT_ENOUGH.code());
                    assertThat(fault.getErrorParameters().get(0)).isEqualTo(memberFrom.getMemberId());
                    assertThat(fault.getErrorParameters().get(1)).isEqualTo(40L);  // memberFrom amount
                    assertThat(fault.getErrorParameters().get(2)).isEqualTo(-50L);  // transfer amount
                })
                .verify();
    }

    @Test
    public void doTransferWhenBalancesExist_Fault_MemberFromNotExists() {
        // Setup
        int memberFromId = -1; // Не существующий member.
        Member memberTo = tu.insertMemberWithBalance("rikTest", 40L, TestUtil.now());

        // Execution
        StepVerifier.create(
                        transferService.doTransferWhenBalancesExist(memberFromId, memberTo.getMemberId(), 50L, TestUtil.now())
                 )
        // Assertion
                .consumeErrorWith(error -> {
                    assertThat(error).isInstanceOf(ServiceFault.class);
                    ServiceFault fault = (ServiceFault) error;
                    assertThat(fault.getCode()).isEqualTo(AppFaultInfo.BALANCE_FOR_MEMBER_NOT_EXISTS.code());
                    assertThat(fault.getErrorParameters().get(0)).isEqualTo(memberFromId);
                 })
                .verify();
    }

    @Test
    public void doTransferWhenBalancesExist_Fault_MemberToNotExists() {
        // Setup
        Member memberFrom = tu.insertMemberWithBalance("rikTest", 40L, TestUtil.now());
        int memberToId = -1; // Не существующий member.

        // Execution
        StepVerifier.create(
                        transferService.doTransferWhenBalancesExist(memberFrom.getMemberId(), memberToId, 50L, TestUtil.now())
                )
        // Assertion
                .consumeErrorWith(error -> {
                    assertThat(error).isInstanceOf(ServiceFault.class);
                    ServiceFault fault = (ServiceFault) error;
                    assertThat(fault.getCode()).isEqualTo(AppFaultInfo.BALANCE_FOR_MEMBER_NOT_EXISTS.code());
                    assertThat(fault.getErrorParameters().get(0)).isEqualTo(memberToId);
                 })
                .verify();
    }

    @Test
    public void doTransferWhenBalancesExist_Fault_BalanceFromNotExists() {
        // Setup
        Member memberFrom = tu.insertMember("rikTest");
        Member memberTo = tu.insertMemberWithBalance("tedTest", 40L, TestUtil.now());

        // Execution
        StepVerifier.create(
                        transferService.doTransferWhenBalancesExist(memberFrom.getMemberId(), memberTo.getMemberId(), 50L, TestUtil.now())
                )
        // Assertion
                .consumeErrorWith(error -> {
                    assertThat(error).isInstanceOf(ServiceFault.class);
                    ServiceFault fault = (ServiceFault) error;
                    assertThat(fault.getCode()).isEqualTo(AppFaultInfo.BALANCE_FOR_MEMBER_NOT_EXISTS.code());
                    assertThat(fault.getErrorParameters().get(0)).isEqualTo(memberFrom.getMemberId());
                 })
                .verify();
    }

    @Test
    public void doTransferWhenBalancesExist_Fault_BalanceToNotExists() {
        // Setup
        Member memberFrom = tu.insertMemberWithBalance("rikTest", 40L, TestUtil.now());
        Member memberTo = tu.insertMember("tedTest");

        // Execution
        StepVerifier.create(
                         transferService.doTransferWhenBalancesExist(memberFrom.getMemberId(), memberTo.getMemberId(), 50L, TestUtil.now())
                 )
        // Assertion
                .consumeErrorWith(error -> {
                    assertThat(error).isInstanceOf(ServiceFault.class);
                    ServiceFault fault = (ServiceFault) error;
                    assertThat(fault.getCode()).isEqualTo(AppFaultInfo.BALANCE_FOR_MEMBER_NOT_EXISTS.code());
                    assertThat(fault.getErrorParameters().get(0)).isEqualTo(memberTo.getMemberId());
                 })
                .verify();
    }


    @Test
    public void doTransfer_WhenToMemberBalanceNotExists() throws InterruptedException {
        // Setup
        Member memberFrom = tu.insertMemberWithBalance("benTest", 40L, TestUtil.now());
        Member memberTo = tu.insertMember("annaTest");
        Thread.sleep(1000);  // Чтобы отличались даты создания и изменения.
        List<Transfer> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                        transferService.doTransfer(memberFrom.getMemberId(), memberTo.getMemberId(), 20L, TestUtil.now())
                )
                .consumeNextWith(result -> listResult.add(result))
                .verifyComplete();

        // Assertion
        Transfer transferOut = listResult.get(0);
        Assert.assertNotNull(transferOut.getTransferId());

        Balance balanceFrom_Db = tu.findBalanceByMemberId(memberFrom.getMemberId());
        Balance balanceTo_Db = tu.findBalanceByMemberId(memberTo.getMemberId());
        Transfer transfer_Db = tu.findTransferByTransferId(transferOut.getTransferId());
        Assert.assertEquals(20L, transfer_Db.getAmount().longValue());

        Assert.assertEquals(40L - 20L, balanceFrom_Db.getAmount().longValue());
        Assert.assertEquals(transfer_Db.getCreatedDate(), balanceFrom_Db.getLastModifiedDate());
        Assert.assertEquals(0L + 20L, balanceTo_Db.getAmount().longValue());
        Assert.assertEquals(transfer_Db.getCreatedDate(), balanceTo_Db.getLastModifiedDate());
    }

//    public void doTransferWhenBalancesExist_MemberToNotExists2() {
//        // Setup
//        Member memberFrom = tu.insertMemberWithBalance("rikTest", 40L, TestUtil.now());
//        int memberToId = -1; // Не существующий member.
//
//        // Execution
//        Mono<Transfer> transfer = transferService.doTransferWhenBalancesExist(memberFrom.getMemberId(), memberToId, 50L, TestUtil.now());
//
//        StepVerifier.create(transfer)
//                // Assertion
//                .consumeErrorWith(error -> {
//                    assertThat(error).isInstanceOf(ServiceFault.class);
//                    ServiceFault fault = (ServiceFault) error;
//                    assertThat(fault.getCode()).isEqualTo(AppFaultInfo.BALANCE_FOR_MEMBER_NOT_EXISTS.code());
//                    assertThat(fault.getErrorParameters().get(0)).isEqualTo(memberToId);
//                 })
//                .verify();
//    }

}
