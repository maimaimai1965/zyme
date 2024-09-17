package ua.mai.zyme.r2dbcmysql;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


@SpringBootTest
@AutoConfigureWebTestClient
@Slf4j
@ActiveProfiles(profiles = "test")
class MemberControllerTest {

  @Autowired
  private WebTestClient webTestClient;

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

//  private void initializeDatabase() {
//    ConnectionFactory connectionFactory = ConnectionFactories.get(dbUrl);
//    R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
//    String query = "CREATE TABLE IF NOT EXISTS member (id SERIAL PRIMARY KEY, name TEXT NOT NULL);";
//    template.getDatabaseClient().sql(query).fetch().rowsUpdated().block();
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
  public void insertMember() {
    // Setup
    Member memberIn = TestUtil.newMember("mikeTest");

    // Execution
    List<Member> list =  webTestClient.post()
            .uri("/api/members")   // Вычитываем все строки из таблицы (не только тестируемые)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(memberIn), Member.class)
            .exchange()
    // Assertion
            .expectStatus().isOk()
            .returnResult(Member.class)
            .getResponseBody()
            .toStream().toList();
    assertTrue(list.size() == 1);
    Member memberOut = list.get(0);
    Member memberDb = tu.findMemberById(memberOut.getMemberId());
    assertEquals(memberOut, memberDb);
  }

  @Test
  public void updateMember() {
    // Setup
    Member memberIn = tu.insertMember("ivanTest");
    memberIn.setName("igorTEST");

    // Execution
    List<Member> list =  webTestClient.put()
            .uri("/api/members")   // Вычитываем все строки из таблицы (не только тестируемые)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(memberIn), Member.class)
            .exchange()
    // Assertion
            .expectStatus().isOk()
            .returnResult(Member.class)
            .getResponseBody()
            .toStream().toList();
    assertTrue(list.size() == 1);
    Member memberOut = list.get(0);
    Member memberDb = tu.findMemberById(memberOut.getMemberId());
    assertEquals(memberOut, memberDb);
  }


  @Test
  public void deleteMemberById() {
    // Setup
    Member memberIn = tu.insertMember("ivanTest");

    // Execution
    List<Member> list =  webTestClient.delete()
            .uri("/api/members/" + memberIn.getMemberId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
    // Assertion
            .expectStatus().isOk()
            .returnResult(Member.class)
            .getResponseBody()
            .toStream().toList();
    assertTrue(list.size() == 0);
    assertNull(tu.findMemberById(memberIn.getMemberId()));
  }


  @Test
  public void deleteMember() {
    // Setup
    Member memberIn = tu.insertMember("ottoTest");

    // Execution
    List<Member> list =  webTestClient.delete()
            .uri("/api/members/" + memberIn.getMemberId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
    // Assertion
            .expectStatus().isOk()
            .returnResult(Member.class)
            .getResponseBody()
            .toStream().toList();
    assertTrue(list.size() == 0);
    assertNull(tu.findMemberById(memberIn.getMemberId()));
  }


  @Test
  public void findAll() {
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
    webTestClient.get()
            .uri("/api/members")   // Вычитываем все строки из таблицы (не только тестируемые)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
    // Assertion
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[*].name")
            // Проверяем на наличие только тестируемых данных.
            .value(hasItems("aniTest", "budiTest", "cepTest", "dodTest"));
  }

  @Test
  public void findById() {
    // Setup
    Member memberIn = tu.insertMember("annaTest");
    Integer id = memberIn.getMemberId();

    // Execution
    webTestClient.get()
            .uri("/api/members/" + id)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
    // Assertion
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.memberId")
            .isEqualTo(id);
  }

  @Test
  public void findByName() {
    // Setup
    Member memberIn = tu.insertMember("annaTest");
    Integer id = memberIn.getMemberId();

    // Execution
    webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                            .path("/api/members")
                            .queryParam("name", "annaTest")
                            .build())
            .accept(MediaType.APPLICATION_JSON)
            .attribute("name", "annaTest")
            .exchange()
            // Assertion
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.memberId")
            .isEqualTo(id)
            .jsonPath("$.name")
            .isEqualTo("annaTest");
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

    // Execution
    List<Member> listOut = webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/api/members")
                    .queryParam("nameLike", "r")
                    .build())   // Вычитываем все строки из таблицы (не только тестируемые)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
    // Assertion
            .expectStatus().isOk()
            .returnResult(Member.class)
            .getResponseBody()
            .toStream().toList();
    Assertions.assertThat(listForCheck).containsExactlyInAnyOrderElementsOf(listOut);
  }

}
