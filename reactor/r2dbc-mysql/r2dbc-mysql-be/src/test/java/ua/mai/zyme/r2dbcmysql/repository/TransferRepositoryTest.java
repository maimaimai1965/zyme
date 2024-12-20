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
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;
import ua.mai.zyme.r2dbcmysql.util.TestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@ContextConfiguration(classes = {R2dbcMysqlApplicationTests.class})
@EnableR2dbcRepositories(basePackages = {"ua.mai.zyme.r2dbcmysql.repository"})
@Import(AppTestConfig.class)
@Slf4j
@ActiveProfiles(profiles = "test")
class TransferRepositoryTest {

    @Autowired
    ConnectionFactory connectionFactory;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TransferRepository transferRepository;

    private TestUtil tu;

    @BeforeEach
    public void setup() {
        tu = new TestUtil();
        tu.setConnectionFactory(connectionFactory);
        tu.setMemberRepository(memberRepository);
        tu.setTransferRepository(transferRepository);
    }


    @AfterEach
    public void cleanup() {
        // Teardown
        tu.deleteTransfersTestData();
        tu.deleteBalancesTestData();
        tu.deleteMembersTestData();
    }


    // ------------------------------------ save(transfer) -------------------------------------------------------------
    @Test
    public void save() {
        // Setup
        Member memberTo = tu.insertMember("annaTest");
        Member memberFrom = tu.insertMember("benTest");

        Transfer transferIn = Transfer.builder()
                .amount(100L)
                .toMemberId(memberTo.getMemberId())
                .fromMemberId(memberFrom.getMemberId())
                .createdDate(tu.now())
                .build();
        List<Transfer> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                transferRepository.save(transferIn))
        // Assertion
                    .recordWith(() -> listResult)
                    .expectNextCount(1)
                    .verifyComplete();

        Transfer transferOut = listResult.get(0);
        assertNotNull(transferOut.getTransferId());
        Transfer transferDb = tu.findTransferByTransferId(transferOut.getTransferId());
        Assertions.assertThat(transferOut)
                .usingRecursiveComparison()
                .isEqualTo(transferDb);
    }


    // ------------------------------------ saveAll(listTransfer) ------------------------------------------------------
    @Test
    public void saveAll() {
        // Setup
        Member memberTo = tu.insertMember("annaTest");
        Member memberFrom = tu.insertMember("benTest");
        Member memberFrom2 = tu.insertMember("mikeTest");

        List<Transfer> listIn = new ArrayList<>(List.of(
                Transfer.builder()
                        .amount(200L)
                        .toMemberId(memberTo.getMemberId())
                        .fromMemberId(memberFrom.getMemberId())
                        .createdDate(tu.now())
                        .build(),
                Transfer.builder()
                        .amount(50L)
                        .toMemberId(memberTo.getMemberId())
                        .fromMemberId(memberFrom2.getMemberId())
                        .createdDate(tu.now())
                        .build()
        ));
        List<Transfer> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                transferRepository.saveAll(listIn))
        // Assertion
                    .recordWith(() -> listResult)
                    .expectNextCount(2)
                    .verifyComplete();

        assertTrue(listResult.size() == 2);
        listResult.forEach(transfer -> {
            assertNotNull(transfer.getTransferId());
        });
        List<Transfer> listDb = List.of(tu.findTransferByTransferId(listResult.get(0).getTransferId()),
                                        tu.findTransferByTransferId(listResult.get(1).getTransferId()));
        Assertions.assertThat(listResult).containsExactlyInAnyOrderElementsOf(listDb);
    }


    // ------------------------------------ deleteById(transferId) -----------------------------------------------------
    @Test
    public void deleteById() {
        // Setup
        Member memberTo = tu.insertMember("rikTest");
        Member memberFrom = tu.insertMember("benTest");
        Member memberFrom2 = tu.insertMember("mikeTest");

        Transfer deletedTransfer = tu.insertTransfer(memberFrom.getMemberId(), memberTo.getMemberId(), 150L, tu.now());
        Transfer existedTransfer = tu.insertTransfer(memberFrom2.getMemberId(), memberTo.getMemberId(), 60L, tu.now());

        // Execution
        StepVerifier.create(
                transferRepository.deleteById(deletedTransfer.getTransferId()))
        // Assertion
                    .verifyComplete();

        assertNull(tu.findTransferByTransferId(deletedTransfer.getTransferId()));
        assertNotNull(tu.findTransferByTransferId(existedTransfer.getTransferId()));
    }


    // ------------------------------------ deleteAllById(listTransferId) ----------------------------------------------
    @Test
    public void deleteAllById() {
        // Setup
        Member memberTo = tu.insertMember("rikTest");
        Member memberFrom = tu.insertMember("benTest");
        Member memberFrom2 = tu.insertMember("mikeTest");
        Member memberFrom3 = tu.insertMember("joanTest");

        Transfer deletedTransfer = tu.insertTransfer(memberFrom.getMemberId(), memberTo.getMemberId(), 150L, tu.now());
        Transfer deletedTransfer2 = tu.insertTransfer(memberFrom.getMemberId(), memberTo.getMemberId(), 40L, tu.now());
        Transfer existedTransfer = tu.insertTransfer(memberFrom.getMemberId(), memberTo.getMemberId(), 60L, tu.now());

        List<Long> deletedIdList = new ArrayList<>(List.of(deletedTransfer.getTransferId(),
                                                           deletedTransfer2.getTransferId()));
        // Execution
        StepVerifier.create(
                transferRepository.deleteAllById(deletedIdList))
        // Assertion
                    .verifyComplete();

        assertNull(tu.findTransferByTransferId(deletedTransfer.getTransferId()));
        assertNull(tu.findTransferByTransferId(deletedTransfer2.getTransferId()));
        assertNotNull(tu.findTransferByTransferId(existedTransfer.getTransferId()));
    }


    // ------------------------------------ findById(transferId) -------------------------------------------------------
    @Test
    public void findById() {
        // Setup
        Member memberTo = tu.insertMember("rikTest");
        Member memberFrom = tu.insertMember("janTest");
        Member memberFrom2 = tu.insertMember("mikeTest");

        Transfer findedTransfer = tu.insertTransfer(memberFrom.getMemberId(), memberTo.getMemberId(), 30L, tu.now());
        Transfer otherTransfer = tu.insertTransfer(memberFrom2.getMemberId(), memberTo.getMemberId(), 20L, tu.now());

        List<Transfer> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                transferRepository.findById(findedTransfer.getTransferId()))
        // Assertion
                    .recordWith(() -> listResult)
                    .expectNextCount(1)
                    .verifyComplete();

        Transfer transferOut = listResult.get(0);
        Assertions.assertThat(findedTransfer.getTransferId()).isEqualTo(transferOut.getTransferId());
    }

    @Test
    public void findByIdWhenNotExists() {
        // Setup
        Member memberTo = tu.insertMember("memberToTest");
        Member memberFrom = tu.insertMember("memberFromTest");
        Member memberFrom2 = tu.insertMember("memberFrom2Test");

        tu.insertTransfer(memberFrom.getMemberId(), memberTo.getMemberId(), 30L, tu.now());
        tu.insertTransfer(memberFrom2.getMemberId(), memberTo.getMemberId(), 20L, tu.now());

        Long noExistsId = -1L;

        // Execution
        StepVerifier.create(
                transferRepository.findById(noExistsId))
        // Assertion
                    .verifyComplete(); // Проверяет получение Mono с пустым значением.
    }


    // ------------------------------------ findAllById(listTransferId) ------------------------------------------------
    @Test
    public void findAllById() {
        // Setup
        Member memberTo    = tu.insertMember("rikTest");
        Member memberFrom  = tu.insertMember("benTest");
        Member memberFrom2 = tu.insertMember("mikeTest");
        Member memberFrom3 = tu.insertMember("joanTest");

        Transfer findedTransfer = tu.insertTransfer(memberFrom.getMemberId(), memberTo.getMemberId(), 10L, tu.now());
        Transfer findedTransfer2 = tu.insertTransfer(memberFrom2.getMemberId(), memberTo.getMemberId(), 140L, tu.now());
        tu.insertTransfer(memberFrom3.getMemberId(), memberTo.getMemberId(), 600L, tu.now());

        List<Transfer> listForCheck = List.of(findedTransfer, findedTransfer2);
        List<Long> listTransferId = List.of(findedTransfer.getTransferId(), findedTransfer2.getTransferId());
        List<Transfer> listResult = new ArrayList<>(2);

        // Execution
        StepVerifier.create(
                transferRepository.findAllById(listTransferId))
        // Assertion
                    .recordWith(() -> listResult)
                    .expectNextCount(2)
                    .verifyComplete();

        Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listResult);
    }


    // ------------------------------------ findByToMemberId(toMemberId) -----------------------------------------------
    @Test
    public void findByToMemberId() {
        // Setup
        Member member1 = tu.insertMember("member1Test");
        Member member2 = tu.insertMember("member2Test");
        Member member3 = tu.insertMember("member3Test");

        Transfer transfer1_2_10 = tu.insertTransfer(member2.getMemberId(), member1.getMemberId(), 10L, tu.now());
        Transfer transfer1_3_20 = tu.insertTransfer(member3.getMemberId(), member1.getMemberId(), 20L, tu.now());
        Transfer transfer2_3_40 = tu.insertTransfer(member3.getMemberId(), member2.getMemberId(), 40L, tu.now());
        Transfer transfer3_1_50 = tu.insertTransfer(member1.getMemberId(), member3.getMemberId(), 50L, tu.now());

        List<Transfer> listForCheck = List.of(transfer1_2_10, transfer1_3_20);
        List<Transfer> listResult = new ArrayList<>(2);

        // Execution
        StepVerifier.create(
                transferRepository.findByToMemberId(member1.getMemberId()))
        // Assertion
                    .recordWith(() -> listResult)
                    .expectNextCount(2)
                    .verifyComplete();

        Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listResult);
    }

}
