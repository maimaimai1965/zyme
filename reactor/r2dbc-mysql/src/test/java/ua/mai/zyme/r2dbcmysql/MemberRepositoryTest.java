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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;


@SpringBootTest
@SpringJUnitConfig
@Slf4j
@ActiveProfiles(profiles = "test")
class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private ConnectionFactory connectionFactory;

  private TestUtil tu;

  @BeforeEach
  public void setup() {
    tu = new TestUtil();
    tu.setConnectionFactory(connectionFactory);
    tu.setMemberRepository(memberRepository);

//    initializeDatabase();
//    insertData();
  }

//  @BeforeEach
//  public void setup() {
//    initializeDatabase();
//    insertData();
//  }
//
//  private void initializeDatabase() {
//    R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
//    String query = "CREATE TABLE IF NOT EXISTS member (id SERIAL PRIMARY KEY, name TEXT NOT NULL);";
//  }
//
//  private void insertData() {
//    Flux<Member> memberFlux = Flux.just(
//        Member.builder().name("ani").build(),
//        Member.builder().name("budi").build(),
//        Member.builder().name("cep").build(),
//        Member.builder().name("dod").build()
//    );
//    memberRepository.deleteAll()
//        .thenMany(memberFlux)
//        .flatMap(memberRepository::save)
//        .doOnNext(member -> log.info("inserted {}", member))
//        .blockLast();
//  }


  @AfterEach
  public void cleanup() {
    // Teardown
    tu.deleteMembersTestData();
  }


  @Test
  public void save() {
    // Setup
    Member memberIn = TestUtil.newMember("annaTest");

    // Execution
    Member memberOut = memberRepository.save(memberIn).block(); // После выполнения в поле memberIn.id прописывается значение.

    // Assertion
    assertNotNull(memberOut.getMemberId());
    Member memberDb = tu.findMemberById(memberOut.getMemberId());
    Assertions.assertThat(memberIn)
              .usingRecursiveComparison()
              .isEqualTo(memberDb);
  }

  @Test
  public void saveAll() {
    // Setup
    List<Member> listIn = new ArrayList<>(List.of(
            TestUtil.newMember("veraTest"),
            TestUtil.newMember("benTest"),
            TestUtil.newMember("tomTest")
    ));

    // Execution
    List<Member> listOut = memberRepository.saveAll(listIn).toStream().toList();

    // Assertion
    assertTrue(listOut.size() == 2);
    listOut.forEach(member -> {
        assertNotNull(member.getMemberId());
    });
    List<Member> listDb = List.of(tu.findMemberById(listOut.get(0).getMemberId()),
                                  tu.findMemberById(listOut.get(1).getMemberId()));
    Assertions.assertThat(listIn).containsExactlyInAnyOrderElementsOf(listOut);
  }


  @Test
  public void deleteById() {
    // Setup
    Member memberIn = tu.insertMember("tomTest");

    // Execution
    memberRepository.deleteById(memberIn.getMemberId()).block();

    // Assertion
    assertNull(tu.findMemberById(memberIn.getMemberId()));
  }

  @Test
  public void deleteAllById() {
    // Setup
    List<Member> list = new ArrayList<>(List.of(
            TestUtil.newMember("aniTest"),
            TestUtil.newMember("budiTest"),
            TestUtil.newMember("cepTest"),
            TestUtil.newMember("dodTest")
    ));
    List<Member> listInserted = new ArrayList<>();
    list.forEach(member -> listInserted.add(tu.insertMember(member)));
    List<Integer> listIdDeleted = new ArrayList<>(List.of(
            listInserted.get(0).getMemberId(),
            listInserted.get(1).getMemberId()));

    // Execution
    memberRepository.deleteAllById(listIdDeleted).block();

    // Assertion
    assertNull(tu.findMemberById(listIdDeleted.get(0)));
    assertNull(tu.findMemberById(listIdDeleted.get(1)));
    assertNotNull(tu.findMemberById(listInserted.get(2).getMemberId()));
    assertNotNull(tu.findMemberById(listInserted.get(3).getMemberId()));
  }


  @Test
  public void findById() {
    // Setup
    Member memberIn = tu.insertMember("annaTest");

    // Execution
    Member memberOut = memberRepository.findById(memberIn.getMemberId()).block();

    // Assertion
    Assertions.assertThat(memberIn.getMemberId()).isEqualTo(memberOut.getMemberId());
  }

  @Test
  public void findByName() {
    // Setup
    Member memberIn = tu.insertMember("kirilTest");

    // Execution
    Member memberOut = memberRepository.findByName(memberIn.getName()).block();

    // Assertion
    Assertions.assertThat(memberIn.getName()).isEqualTo(memberOut.getName());
  }

  @Test
  public void findByIdWhenNotExists() {
    // Setup
    Integer noExistsId = -1;

    // Execution
    Member memberOut = memberRepository.findById(noExistsId).block();

    // Assertion
    assertNull(memberOut);
  }

  @Test
  public void findAll() throws InterruptedException {
    // Setup
    List<Member> list = new ArrayList<>(List.of(
            TestUtil.newMember("aniTest"),
            TestUtil.newMember("budiTest"),
            TestUtil.newMember("cepTest"),
            TestUtil.newMember("dodTest")
    ));
    List<Member> listSaved = new ArrayList<>();
    list.forEach(member -> listSaved.add(tu.insertMember(member)));

    // Execution
    List<Member> listOut = memberRepository.findAll()
            .filter(member -> TestUtil.isMemberForTest(member))
            .toStream()
            .toList();

    // Assertion
    Assertions.assertThat(list).containsExactlyInAnyOrderElementsOf(listOut);
  }


  @Test
  public void findAllById() throws InterruptedException {
    // Setup
    List<Member> list = new ArrayList<>(List.of(
            TestUtil.newMember("aniTest"),
            TestUtil.newMember("budiTest"),
            TestUtil.newMember("cepTest"),
            TestUtil.newMember("dodTest")
    ));
    List<Member> listSaved = new ArrayList<>();
    list.forEach(member -> listSaved.add(tu.insertMember(member)));
    List<Member> listForCheck = List.of(listSaved.get(0), listSaved.get(3));
    List<Integer> listIdForCheck = listForCheck.stream().map(member -> member.getMemberId()).toList();

    // Execution
    List<Member> listOut = memberRepository.findAllById(listIdForCheck)
            .filter(member -> TestUtil.isMemberForTest(member))
            .toStream()
            .toList();

    // Assertion
    Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listOut);
  }


  @Test
  public void findByNameLike() throws InterruptedException {
    // Setup
    List<Member> list = new ArrayList<>(List.of(
            TestUtil.newMember("vinsenTest"),
            TestUtil.newMember("bearnTest"),
            TestUtil.newMember("tomTest"),
            TestUtil.newMember("redlTest")
    ));
    List<Member> listSaved = new ArrayList<>();
    list.forEach(member -> listSaved.add(tu.insertMember(member)));
    List<Member> listForCheck = list.stream().filter(member -> member.getName().contains("r")).toList();

    // Execution
    List<Member> listOut = memberRepository.findByNameLike("%r%")
            .filter(member -> TestUtil.isMemberForTest(member))
            .toStream()
            .toList();

    // Assertion
    Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listOut);
  }


  @Test
  public void findByNameLengthLE() throws InterruptedException {
    // Setup
    List<Member> list = new ArrayList<>(List.of(
            TestUtil.newMember("vinTest"),
            TestUtil.newMember("bearnTest"),
            TestUtil.newMember("tomTest"),
            TestUtil.newMember("redlTest")
    ));
    List<Member> listSaved = new ArrayList<>();
    list.forEach(member -> listSaved.add(tu.insertMember(member)));
    List<Member> listForCheck = list.stream().filter(member -> member.getName().length() <= 3).toList();

    // Execution
    List<Member> listOut = memberRepository.findByNameLengthLE(3)
            .filter(member -> TestUtil.isMemberForTest(member))
            .toStream()
            .toList();

    // Assertion
    Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listOut);
  }

}
