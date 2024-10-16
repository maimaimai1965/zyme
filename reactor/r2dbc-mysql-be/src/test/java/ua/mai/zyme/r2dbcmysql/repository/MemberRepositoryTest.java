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
class MemberRepositoryTest {

    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private MemberRepository memberRepository;

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
    public void saveAsInsert() {
        // Setup
        Member memberIn = TestUtil.newMember("annaTest");
        List<Member> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                memberRepository.save(memberIn))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Member memberOut = listResult.get(0);
        assertNotNull(memberOut.getMemberId());
        Member member_Db = tu.findMemberByMemberId(memberOut.getMemberId());
        Assertions.assertThat(member_Db)
                .usingRecursiveComparison()
                .isEqualTo(memberOut);
    }

    @Test
    public void saveAsUpdate() {
        // Setup
        Member memberIn = tu.insertMember("billTest");
        memberIn.setName("bill2Test");
        List<Member> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                memberRepository.save(memberIn))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Member memberOut = listResult.get(0);
        Assertions.assertThat(memberOut)
                .usingRecursiveComparison()
                .isEqualTo(memberIn);
        Member member_Db = tu.findMemberByMemberId(memberOut.getMemberId());
        Assertions.assertThat(member_Db)
                .usingRecursiveComparison()
                .isEqualTo(memberIn);
    }

    @Test
    public void saveAllAsInsert() {
        // Setup
        List<Member> listIn = new ArrayList<>(List.of(
                TestUtil.newMember("veraTest"),
                TestUtil.newMember("benTest"),
                TestUtil.newMember("tomTest")
        ));
        List<Member> listResult = new ArrayList<>();

        // Execution
        StepVerifier.create(
                memberRepository.saveAll(listIn))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .consumeNextWith(result -> listResult.add(result))
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        assertTrue(listResult.size() == 3);
        listResult.forEach(member -> {
            assertNotNull(member.getMemberId());
        });
        List<Member> list_Db = List.of(
                tu.findMemberByMemberId(listResult.get(0).getMemberId()),
                tu.findMemberByMemberId(listResult.get(1).getMemberId()),
                tu.findMemberByMemberId(listResult.get(2).getMemberId()));
        Assertions.assertThat(list_Db).containsExactlyInAnyOrderElementsOf(listResult);
    }

    @Test
    public void saveAllAsInsertAndUpdate() {
        // Setup
        Member memberIn = tu.insertMember("billTest");
        memberIn.setName("bill2Test");

        List<Member> listIn = new ArrayList<>(List.of(
                memberIn,
                TestUtil.newMember("tomTest")
        ));
        List<Member> listResult = new ArrayList<>();

        // Execution
        StepVerifier.create(
                memberRepository.saveAll(listIn))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        assertTrue(listResult.size() == 2);
        listResult.forEach(member -> {
            assertNotNull(member.getMemberId());
        });
        List<Member> list_Db = List.of(
                tu.findMemberByMemberId(listResult.get(0).getMemberId()),
                tu.findMemberByMemberId(listResult.get(1).getMemberId()));
        Assertions.assertThat(list_Db).containsExactlyInAnyOrderElementsOf(listResult);
    }

    @Test
    public void deleteById() {
        // Setup
        Member memberIn = tu.insertMember("tomTest");

        // Execution
        memberRepository.deleteById(memberIn.getMemberId()).block();

        // Execution
        StepVerifier.create(
                memberRepository.deleteById(memberIn.getMemberId()))
        // Assertion
                    .verifyComplete();

        assertNull(tu.findMemberByMemberId(memberIn.getMemberId()));
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
        StepVerifier.create(
                memberRepository.deleteAllById(listIdDeleted))
        // Assertion
                    .verifyComplete();

        assertNull(tu.findMemberByMemberId(listIdDeleted.get(0)));
        assertNull(tu.findMemberByMemberId(listIdDeleted.get(1)));
        assertNotNull(tu.findMemberByMemberId(listInserted.get(2).getMemberId()));
        assertNotNull(tu.findMemberByMemberId(listInserted.get(3).getMemberId()));
    }

    @Test
    public void findById() {
        // Setup
        Member memberIn = tu.insertMember("annaTest");
        Member memberIn2 = tu.insertMember("anna2Test");
        List<Member> listResult = new ArrayList<>();

        // Execution
        StepVerifier.create(
                memberRepository.findById(memberIn.getMemberId()))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Member memberOut = listResult.get(0);
        Assertions.assertThat(memberIn.getMemberId()).isEqualTo(memberOut.getMemberId());
    }

    @Test
    public void findByName() {
        // Setup
        Member memberIn = tu.insertMember("kirilTest");
        List<Member> listResult = new ArrayList<>();

        // Execution
        StepVerifier.create(
                memberRepository.findByName(memberIn.getName()))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Member memberOut = listResult.get(0);
        Assertions.assertThat(memberIn.getName()).isEqualTo(memberOut.getName());
    }

    @Test
    public void findById_WhenMemberNotExists() {
        // Setup
        Integer notExistedMemberId = -1;

        // Execution
        StepVerifier.create(
                memberRepository.findById(notExistedMemberId))
        // Assertion
                   .verifyComplete(); // Проверяет получение Mono с пустым значением.
    }

    @Test
    public void findAll() throws InterruptedException {
        // Setup
        List<Member> listIn = new ArrayList<>(List.of(
                tu.insertMember("aniTest"),
                tu.insertMember("budiTest"),
                tu.insertMember("cepTest"),
                tu.insertMember("dodTest")
        ));
        List<Member> listResult = new ArrayList<>();

        // Execution
        StepVerifier.create(
                memberRepository.findAll()                                           // Вычитываем все данные.
                                .filter(member -> TestUtil.isMemberForTest(member))) // Проверяем только на тестируемых данных.
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .consumeNextWith(result -> listResult.add(result))
                    .consumeNextWith(result -> listResult.add(result))
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Assertions.assertThat(listResult)
                  .containsExactlyInAnyOrderElementsOf(listIn);
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
        List<Member> listResult = new ArrayList<>();

        // Execution
        StepVerifier.create(
                memberRepository.findAllById(listIdForCheck))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        // Assertion
        Assertions.assertThat(listResult).containsExactlyInAnyOrderElementsOf(listForCheck);
    }

    @Test
    public void findByNameLike() {
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
        List<Member> listResult = new ArrayList<>();

        // Execution
        StepVerifier.create(
                memberRepository.findByNameLike("%r%"))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Assertions.assertThat(listResult).containsExactlyInAnyOrderElementsOf(listForCheck);
    }

    @Test
    public void findByNameLengthLE() {
        // Setup
        List<Member> list = new ArrayList<>(List.of(
                TestUtil.newMember("vinTest"),
                TestUtil.newMember("bearnTest"),
                TestUtil.newMember("tomTest"),
                TestUtil.newMember("redlTest")
        ));
        List<Member> listSaved = new ArrayList<>();
        list.forEach(member -> listSaved.add(tu.insertMember(member)));
        List<Member> listForCheck = list.stream().filter(member -> member.getName().length() <= 7).toList();
        List<Member> listResult = new ArrayList<>();

        // Execution
        StepVerifier.create(
                memberRepository.findByNameLengthLE(7))
        // Assertion
                    .consumeNextWith(result -> listResult.add(result))
                    .consumeNextWith(result -> listResult.add(result))
                    .verifyComplete();

        Assertions.assertThat(listResult).containsExactlyInAnyOrderElementsOf(listForCheck);
    }

    @Test
    public void insertThroughSql() {
        // Setup
        String name = "memberTest";

        // Execution
        StepVerifier.create(
                memberRepository.insertThroughSql(name))
        // Assertion
                    .verifyComplete();

        Member memberDb = memberRepository.findByName(name).block();
        assertNotNull(memberDb);
    }

    @Test
    public void updateThroughSql() {
        // Setup
        Member memberIn = tu.insertMember("jilTest");
        memberIn.setName("jil2Test");

        // Execution
        StepVerifier.create(
                memberRepository.updateThroughSql(memberIn.getMemberId(), memberIn.getName()))
        // Assertion
                    .verifyComplete();

        Member member_Db = tu.findMemberByMemberId(memberIn.getMemberId());
        Assertions.assertThat(member_Db)
                .usingRecursiveComparison()
                .isEqualTo(memberIn);
    }

    @Test
    public void deleteThroughSql() {
        // Setup
        Member memberIn = tu.insertMember("mikeTest");

        // Execution
        StepVerifier.create(
                memberRepository.deleteThroughSql(memberIn.getMemberId()))
        // Assertion
                    .verifyComplete();

        Member member_Db = tu.findMemberByMemberId(memberIn.getMemberId());
        assertNull(member_Db);
    }

}
