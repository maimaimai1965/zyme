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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@SpringBootTest
@SpringJUnitConfig
@Slf4j
@ActiveProfiles(profiles = "test")
class TransferRepositoryTest {

  @Autowired
  private TransferRepository transferRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  ConnectionFactory connectionFactory;

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
    Transfer transferDb = tu.findTransferById(transferOut.getTransferId());
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
    List<Transfer> listDb = List.of(tu.findTransferById(listOut.get(0).getTransferId()),
                                    tu.findTransferById(listOut.get(1).getTransferId()));
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
    assertNull(tu.findTransferById(deletedTransfer.getTransferId()));
    assertNotNull(tu.findTransferById(existedTransfer.getTransferId()));
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
    assertNull(tu.findTransferById(deletedTransfer.getTransferId()));
    assertNull(tu.findTransferById(deletedTransfer2.getTransferId()));
    assertNotNull(tu.findTransferById(existedTransfer.getTransferId()));
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
    Member memberTo = tu.insertMember("rikTest");
    Member memberFrom = tu.insertMember("janTest");
    Member memberFrom2 = tu.insertMember("mikeTest");

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
  public void findAllById() throws InterruptedException {
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

//  @Test
//  public void findByNameLengthLE() throws InterruptedException {
//    // Setup
//    List<Member> list = new ArrayList<>(List.of(
//            newMember("vinTest"),
//            newMember("bearnTest"),
//            newMember("tomTest"),
//            newMember("redlTest")
//    ));
//    List<Member> listSaved = new ArrayList<>();
//    list.forEach(member -> listSaved.add(insertMember(member)));
//    List<Member> listForCheck = list.stream().filter(member -> member.getName().length() <= 3).toList();
//
//    // Execution
//    List<Member> listOut = transferRepository.findByNameLengthLE(3)
//            .filter(member -> isMemberForTest(member))
//            .toStream()
//            .toList();
//
//    // Assertion
//    Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listOut);
//  }

}
