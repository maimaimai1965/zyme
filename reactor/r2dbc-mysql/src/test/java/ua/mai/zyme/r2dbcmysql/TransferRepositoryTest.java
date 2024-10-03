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
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.repository.TransferRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@SpringBootTest
@SpringJUnitConfig
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
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Transfer transferOut = listResult.get(0);
        assertNotNull(transferOut.getTransferId());
        Transfer transferDb = tu.findTransferByTransferId(transferOut.getTransferId());
        Assertions.assertThat(transferOut)
                .usingRecursiveComparison()
                .isEqualTo(transferDb);
    }

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
                    .consumeNextWith(result -> listResult.add(result))
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        assertTrue(listResult.size() == 2);
        listResult.forEach(transfer -> {
            assertNotNull(transfer.getTransferId());
        });
        List<Transfer> listDb = List.of(tu.findTransferByTransferId(listResult.get(0).getTransferId()),
                tu.findTransferByTransferId(listResult.get(1).getTransferId()));
        Assertions.assertThat(listResult).containsExactlyInAnyOrderElementsOf(listDb);
    }


    @Test
    public void deleteById() {
        // Setup
        Member memberTo = tu.insertMember("rikTest");
        Member memberFrom = tu.insertMember("benTest");
        Member memberFrom2 = tu.insertMember("mikeTest");

        Transfer deletedTransfer = tu.insertTransfer(150L, memberTo.getMemberId(), memberFrom.getMemberId(), tu.now());
        Transfer existedTransfer = tu.insertTransfer(60L, memberTo.getMemberId(), memberFrom2.getMemberId(), tu.now());

        // Execution
        StepVerifier.create(
                transferRepository.deleteById(deletedTransfer.getTransferId()))
        // Assertion
                    .verifyComplete();

        assertNull(tu.findTransferByTransferId(deletedTransfer.getTransferId()));
        assertNotNull(tu.findTransferByTransferId(existedTransfer.getTransferId()));
    }

    @Test
    public void deleteAllById() {
        // Setup
        Member memberTo = tu.insertMember("rikTest");
        Member memberFrom = tu.insertMember("benTest");
        Member memberFrom2 = tu.insertMember("mikeTest");
        Member memberFrom3 = tu.insertMember("joanTest");

        Transfer deletedTransfer = tu.insertTransfer(150L, memberTo.getMemberId(), memberFrom.getMemberId(), tu.now());
        Transfer deletedTransfer2 = tu.insertTransfer(40L, memberTo.getMemberId(), memberFrom2.getMemberId(), tu.now());
        Transfer existedTransfer = tu.insertTransfer(60L, memberTo.getMemberId(), memberFrom3.getMemberId(), tu.now());

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

    @Test
    public void findById() {
        // Setup
        Member memberTo = tu.insertMember("rikTest");
        Member memberFrom = tu.insertMember("janTest");
        Member memberFrom2 = tu.insertMember("mikeTest");

        Transfer findedTransfer = tu.insertTransfer(30L, memberTo.getMemberId(), memberFrom.getMemberId(), tu.now());
        Transfer otherTransfer = tu.insertTransfer(20L, memberTo.getMemberId(), memberFrom2.getMemberId(), tu.now());

        List<Transfer> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                transferRepository.findById(findedTransfer.getTransferId()))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
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

        tu.insertTransfer(30L, memberTo.getMemberId(), memberFrom.getMemberId(), tu.now());
        tu.insertTransfer(20L, memberTo.getMemberId(), memberFrom2.getMemberId(), tu.now());

        Long noExistsId = -1L;

        // Execution
        StepVerifier.create(
                transferRepository.findById(noExistsId))
        // Assertion
                    .verifyComplete(); // Проверяет получение Mono с пустым значением.
    }

    @Test
    public void findAllById() {
        // Setup
        Member memberTo    = tu.insertMember("rikTest");
        Member memberFrom  = tu.insertMember("benTest");
        Member memberFrom2 = tu.insertMember("mikeTest");
        Member memberFrom3 = tu.insertMember("joanTest");

        Transfer findedTransfer = tu.insertTransfer(10L, memberTo.getMemberId(), memberFrom.getMemberId(), tu.now());
        Transfer findedTransfer2 = tu.insertTransfer(140L, memberTo.getMemberId(), memberFrom2.getMemberId(), tu.now());
        tu.insertTransfer(600L, memberTo.getMemberId(), memberFrom3.getMemberId(), tu.now());

        List<Transfer> listForCheck = List.of(findedTransfer, findedTransfer2);
        List<Long> listTransferId = List.of(findedTransfer.getTransferId(), findedTransfer2.getTransferId());
        List<Transfer> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                transferRepository.findAllById(listTransferId))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listResult);
    }

    @Test
    public void findByToMemberId() {
        // Setup
        Member member1 = tu.insertMember("member1Test");
        Member member2 = tu.insertMember("member2Test");
        Member member3 = tu.insertMember("member3Test");

        Transfer transfer1_2_10 = tu.insertTransfer(10L, member1.getMemberId(), member2.getMemberId(), tu.now());
        Transfer transfer1_3_20 = tu.insertTransfer(20L, member1.getMemberId(), member3.getMemberId(), tu.now());
        Transfer transfer2_3_40 = tu.insertTransfer(40L, member2.getMemberId(), member3.getMemberId(), tu.now());
        Transfer transfer3_1_50 = tu.insertTransfer(50L, member3.getMemberId(), member1.getMemberId(), tu.now());

        List<Transfer> listForCheck = List.of(transfer1_2_10, transfer1_3_20);
        List<Transfer> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                transferRepository.findByToMemberId(member1.getMemberId()))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listResult);
    }

}
