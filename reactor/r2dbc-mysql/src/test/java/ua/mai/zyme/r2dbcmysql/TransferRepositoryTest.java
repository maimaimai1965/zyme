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

    // Execution
    Transfer transferOut = transferRepository.save(transferIn).block(); // После выполнения в поле memberIn.id прописывается значение.

    // Assertion
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

    // Execution
    List<Transfer> listOut = transferRepository.saveAll(listIn).toStream().toList();

    // Assertion
    assertTrue(listOut.size() == 2);
    listOut.forEach(transfer -> {
        assertNotNull(transfer.getTransferId());
    });
    List<Transfer> listDb = List.of(tu.findTransferByTransferId(listOut.get(0).getTransferId()),
                                    tu.findTransferByTransferId(listOut.get(1).getTransferId()));
    Assertions.assertThat(listOut).containsExactlyInAnyOrderElementsOf(listDb);
  }


  @Test
  public void deleteById() {
    // Setup
    Member memberTo = tu.insertMember("rikTest");
    Member memberFrom = tu.insertMember("benTest");
    Member memberFrom2 = tu.insertMember("mikeTest");

    Transfer deletedTransfer = tu.insertTransfer(
            Transfer.builder()
                    .amount(150L)
                    .toMemberId(memberTo.getMemberId())
                    .fromMemberId(memberFrom.getMemberId())
                    .createdDate(tu.now())
                    .build()
    );
    Transfer existedTransfer =  tu.insertTransfer(
            Transfer.builder()
                    .amount(60L)
                    .toMemberId(memberTo.getMemberId())
                    .fromMemberId(memberFrom2.getMemberId())
                    .createdDate(tu.now())
                    .build()
    );

    // Execution
    transferRepository.deleteById(deletedTransfer.getTransferId()).block();

    // Assertion
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

    Transfer deletedTransfer = tu.insertTransfer(
            Transfer.builder()
                    .amount(150L)
                    .toMemberId(memberTo.getMemberId())
                    .fromMemberId(memberFrom.getMemberId())
                    .createdDate(tu.now())
                    .build()
    );
    Transfer deletedTransfer2 = tu.insertTransfer(
            Transfer.builder()
                    .amount(40L)
                    .toMemberId(memberTo.getMemberId())
                    .fromMemberId(memberFrom2.getMemberId())
                    .createdDate(tu.now())
                    .build()
    );
    Transfer existedTransfer =  tu.insertTransfer(
            Transfer.builder()
                    .amount(60L)
                    .toMemberId(memberTo.getMemberId())
                    .fromMemberId(memberFrom3.getMemberId())
                    .createdDate(tu.now())
                    .build()
    );

    // Execution
    transferRepository.deleteAllById(new ArrayList<>(List.of(deletedTransfer.getTransferId(),
                                                             deletedTransfer2.getTransferId())))
                      .block();

    // Assertion
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

    Transfer findedTransfer = tu.insertTransfer(
            Transfer.builder()
                    .amount(30L)
                    .toMemberId(memberTo.getMemberId())
                    .fromMemberId(memberFrom.getMemberId())
                    .createdDate(tu.now())
                    .build()
    );
    Transfer otherTransfer =  tu.insertTransfer(
            Transfer.builder()
                    .amount(20L)
                    .toMemberId(memberTo.getMemberId())
                    .fromMemberId(memberFrom2.getMemberId())
                    .createdDate(tu.now())
                    .build()
    );

    // Execution
    Transfer transferOut = transferRepository.findById(findedTransfer.getTransferId()).block();

    // Assertion
    Assertions.assertThat(findedTransfer.getTransferId()).isEqualTo(transferOut.getTransferId());
  }

  @Test
  public void findByIdWhenNotExists() {
    // Setup
    Member memberTo = tu.insertMember("memberToTest");
    Member memberFrom = tu.insertMember("memberFromTest");
    Member memberFrom2 = tu.insertMember("memberFrom2Test");

    tu.insertTransfer(
            Transfer.builder()
                    .amount(30L)
                    .toMemberId(memberTo.getMemberId())
                    .fromMemberId(memberFrom.getMemberId())
                    .createdDate(tu.now())
                    .build()
    );
    tu.insertTransfer(
            Transfer.builder()
                    .amount(20L)
                    .toMemberId(memberTo.getMemberId())
                    .fromMemberId(memberFrom2.getMemberId())
                    .createdDate(tu.now())
                    .build()
    );
    Long noExistsId = -1L;

    // Execution
    Transfer transferOut = transferRepository.findById(noExistsId).block();

    // Assertion
    assertNull(transferOut);
  }

  @Test
  public void findAllById() {
    // Setup
    Member memberTo    = tu.insertMember("rikTest");
    Member memberFrom  = tu.insertMember("benTest");
    Member memberFrom2 = tu.insertMember("mikeTest");
    Member memberFrom3 = tu.insertMember("joanTest");

    Transfer findedTransfer = tu.insertTransfer(
            Transfer.builder()
                    .amount(10L)
                    .toMemberId(memberTo.getMemberId())
                    .fromMemberId(memberFrom.getMemberId())
                    .createdDate(tu.now())
                    .build()
    );
    Transfer findedTransfer2 = tu.insertTransfer(
            Transfer.builder()
                    .amount(140L)
                    .toMemberId(memberTo.getMemberId())
                    .fromMemberId(memberFrom2.getMemberId())
                    .createdDate(tu.now())
                    .build()
    );
    tu.insertTransfer(
            Transfer.builder()
                    .amount(600L)
                    .toMemberId(memberTo.getMemberId())
                    .fromMemberId(memberFrom3.getMemberId())
                    .createdDate(tu.now())
                    .build()
    );
    List<Transfer> listForCheck = List.of(findedTransfer, findedTransfer2);

    // Execution
    List<Transfer> listOut = transferRepository.findAllById(List.of(findedTransfer.getTransferId(),
                                                                    findedTransfer2.getTransferId()))
            .toStream()
            .toList();

    // Assertion
    Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listOut);
  }

  @Test
  public void findByToMemberId() {
    // Setup
    Member member1 = tu.insertMember("member1Test");
    Member member2 = tu.insertMember("member2Test");
    Member member3 = tu.insertMember("member3Test");

    Transfer transfer1_2_10 = Transfer.builder()
            .amount(10L)
            .toMemberId(member1.getMemberId())
            .fromMemberId(member2.getMemberId())
            .createdDate(tu.now())
            .build();
    tu.insertTransfer(transfer1_2_10);
    Transfer transfer1_3_20 = Transfer.builder()
            .amount(20L)
            .toMemberId(member1.getMemberId())
            .fromMemberId(member3.getMemberId())
            .createdDate(tu.now())
            .build();
    tu.insertTransfer(transfer1_3_20);
    Transfer transfer2_3_40 = Transfer.builder()
            .amount(40L)
            .toMemberId(member2.getMemberId())
            .fromMemberId(member3.getMemberId())
            .createdDate(tu.now())
            .build();
    tu.insertTransfer(transfer2_3_40);
    Transfer transfer3_1_50 = Transfer.builder()
            .amount(50L)
            .toMemberId(member3.getMemberId())
            .fromMemberId(member1.getMemberId())
            .createdDate(tu.now())
            .build();
    tu.insertTransfer(transfer3_1_50);

    List<Transfer> listForCheck = List.of(transfer1_2_10, transfer1_3_20);

    // Execution
    List<Transfer> listOut = transferRepository.findByToMemberId(member1.getMemberId())
            .toStream().toList();

    // Assertion
    Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listOut);
  }

}
