package ua.mai.zyme.r2dbcmysql.controller;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ua.mai.zyme.r2dbcmysql.R2dbcMysqlApplicationTests;
import ua.mai.zyme.r2dbcmysql.config.AppTestConfig;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.exception.FaultInfo;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.util.TestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // Запуск всего контекста на
@ContextConfiguration(classes = {R2dbcMysqlApplicationTests.class})
@EnableR2dbcRepositories(basePackages = {"ua.mai.zyme.r2dbcmysql.repository"})
@ComponentScan(basePackages = {
        "ua.mai.zyme.r2dbcmysql.service",
        "ua.mai.zyme.r2dbcmysql.controller",
        "ua.mai.zyme.r2dbcmysql.exception",
})
@Import(AppTestConfig.class)
@EnableAutoConfiguration
@AutoConfigureWebTestClient
@Slf4j
@ActiveProfiles(profiles = "test")
class MemberControllerTest {

    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WebTestClient webTestClient;

    private TestUtil tu;

    @BeforeEach
    public void setup() {
        tu = new TestUtil();
        tu.setConnectionFactory(connectionFactory);
        tu.setMemberRepository(memberRepository);
    }

    @AfterEach
    public void cleanup() {
        // Teardown
        tu.deleteMembersTestData();
    }


    // ------------------------------------ insertMember(member) <- /api/members ---------------------------------------
    @Test
    public void insertMember() {
        // Setup
        Member memberIn = TestUtil.newMember("mikeTest");

        // Execution
        List<Member> listResult =  webTestClient
                .post()
                .uri("/api/members")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(memberIn), Member.class)
                .exchange()
        // Assertion
                .expectStatus().isOk()  // 200
                .returnResult(Member.class)
                .getResponseBody()
                .toStream().toList();

        assertTrue(listResult.size() == 1);
        Member memberOut = listResult.get(0);
        Member member_Db = tu.findMemberByMemberId(memberOut.getMemberId());
        assertEquals(memberOut, member_Db);
    }

    @Test
    public void insertMember_ERR101_NotNullNewMemberId() {
        // Setup
        Member memberIn = TestUtil.newMember("mikeTest");
        memberIn.setMemberId(-1);

        // Execution
        webTestClient
                .post()
                .uri("/api/members")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(memberIn), Member.class)
                .exchange()
        // Assertion
                .expectStatus().value(status -> assertEquals("500", status.toString()))
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(response.getResponseBody());
                    assertTrue(body.contains(AppFaultInfo.NEW_MEMBER_ID_MUST_BE_NULL.code()));
                });
    }


    // ------------------------------------ insertMembers(fluxMember) <- /api/members/flux -----------------------------
    @Test
    public void insertMembers() {
        // Setup
        Member memberIn1 = TestUtil.newMember("mikeTest");
        Member memberIn2 = TestUtil.newMember("rikTest");

        // Execution
        List<Member> listResult =  webTestClient
                .post()
                .uri("/api/members/flux")
                .accept(MediaType.APPLICATION_JSON)
                .body(Flux.just(memberIn1, memberIn2), Member.class)
                .exchange()
        // Assertion
                .expectStatus().isOk()  // 200
                .returnResult(Member.class)
                .getResponseBody()
                .toStream().toList();

        assertTrue(listResult.size() == 2);

        int memberId1 = listResult.get(0).getMemberId();
        int memberId2 = listResult.get(1).getMemberId();
        List<Member> list_Db = List.of(tu.findMemberByMemberId(memberId1),
                                       tu.findMemberByMemberId(memberId2));
        Assertions.assertThat(listResult).containsExactlyInAnyOrderElementsOf(list_Db);
    }

    @Test
    public void insertMembers_Fault_WhenNewMemberHasMemberId() {
        // Setup
        Member memberIn1 = TestUtil.newMember("mikeTest");
        Member memberIn2 = TestUtil.newMember("rikTest");
        memberIn2.setMemberId(-1);

        // Execution
        webTestClient
                .post()
                .uri("/api/members/flux")
                .accept(MediaType.APPLICATION_JSON)
                .body(Flux.just(memberIn1, memberIn2), Member.class)
                .exchange()
        // Assertion
                .expectStatus().value(status -> assertEquals("500", status.toString()))
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(response.getResponseBody());
                    assertTrue(body.contains(AppFaultInfo.NEW_MEMBER_ID_MUST_BE_NULL.code()));
                 });
    }


    // ------------------------------------ updateMember(member) <- /api/members ---------------------------------------
    @Test
    public void updateMember() {
        // Setup
        Member memberIn = tu.insertMember("ivanTest");
        memberIn.setName("igorTEST");

        // Execution
        List<Member> listResult =  webTestClient
                .put()
                .uri("/api/members")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(memberIn), Member.class)
                .exchange()
        // Assertion
                .expectStatus().isOk()  // 200
                .returnResult(Member.class)
                .getResponseBody()
                .toStream().toList();

        assertTrue(listResult.size() == 1);
        Member memberOut = listResult.get(0);
        assertEquals(memberIn, memberOut);
        Member member_Db = tu.findMemberByMemberId(memberOut.getMemberId());
        assertEquals(memberIn, member_Db);
    }


    // ------------------------------------ deleteMemberById(id) <- /api/members/{id} ----------------------------------
    @Test
    public void deleteMemberById() {
        // Setup
        Member memberIn = tu.insertMember("ivanTest");

        // Execution
        List<Member> listResult =  webTestClient
                .delete()
                .uri("/api/members/" + memberIn.getMemberId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().isOk()  // 200
                .returnResult(Member.class)
                .getResponseBody()
                .toStream().toList();

        assertTrue(listResult.size() == 0);
        assertNull(tu.findMemberByMemberId(memberIn.getMemberId()));
    }

    @Test
    public void deleteMemberById_WhenMemberNotExists() {
        // Execution
        List<Member> listResult =  webTestClient
                .delete()
                .uri("/api/members/" + -1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().isOk()  // 200
                .returnResult(Member.class)
                .getResponseBody()
                .toStream().toList();

        assertTrue(listResult.size() == 0);
    }


    // ------------------------------------ findAll() <- /api/members --------------------------------------------------
    @Test
    public void findAll() {
        // Setup
        List<Member> listIn = new ArrayList<>(List.of(
                tu.insertMember("aniTest"),
                tu.insertMember("budiTest"),
                tu.insertMember("cepTest"),
                tu.insertMember("dodTest")
        ));

        // Execution
        List<Member> listResult = webTestClient
                .get()
                .uri("/api/members")   // Вычитываем все строки из таблицы (не только тестируемые)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().isOk()  // 200
                .returnResult(Member.class)
                .getResponseBody()
                .toStream()
                .filter(member -> TestUtil.isMemberForTest(member))  // Проверяем только на тестируемых данных
                .toList();

        Assertions.assertThat(listResult)
                  .containsExactlyInAnyOrderElementsOf(listIn);
    }


    // ------------------------------------ findMemberByMemberId(memberId) <- /api/members/{memberId} ------------------
    @Test
    public void findMemberByMemberId() {
        // Setup
        Member memberIn = tu.insertMember("annaTest");
        Integer id = memberIn.getMemberId();

        // Execution
        List<Member> listResult =  webTestClient
                .get()
                .uri("/api/members/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().isOk()  // 200
                .returnResult(Member.class)
                .getResponseBody()
                .toStream().toList();

        assertTrue(listResult.size() == 1);
        Member memberOut = listResult.get(0);
        assertEquals(memberIn, memberOut);
    }

    @Test
    public void findMemberByMemberId_ERR002_MemberNotFound() {
        // Setup
        Integer badMemberId = 1;

        // Execution
        webTestClient
                .get()
                .uri("/api/members/" + badMemberId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().value(status -> assertEquals("404", status.toString()))
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(response.getResponseBody());
                    assertTrue(body.contains(FaultInfo.NOT_FOUND_CODE));
                });
    }

    @Test
    public void findMemberByMemberId_ERR001_BadMemberId() {
        // Setup
        String badMemberId = "12BAD";

        // Execution
        webTestClient
                .get()
                .uri("/api/members/" + badMemberId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().value(status -> assertEquals("400", status.toString()))
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(response.getResponseBody());
                    assertTrue(body.contains(FaultInfo.UNEXPECTED_ERROR_CODE));
                });
    }

    @Test
    public void findMemberByMemberId_ERR001_MethodNotAllowed() {
        // Execution
        webTestClient
                .put()
                .uri("/api/members/" + 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().value(status -> assertEquals("405", status.toString()))
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(response.getResponseBody());
                    assertTrue(body.contains(FaultInfo.UNEXPECTED_ERROR_CODE));
                });
    }

    @Test
    public void findMemberByMemberId_ERR001_NoStaticResource() {
        // Execution
        webTestClient
                .put()
                .uri("/api/memb")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().value(status -> assertEquals("404", status.toString()))
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(response.getResponseBody());
                    assertTrue(body.contains(FaultInfo.UNEXPECTED_ERROR_CODE));
                });
    }


    // ------------------------------------ findMemberByName(name) <- /api/members?name= -------------------------------
    @Test
    public void findMemberByName() {
        // Setup
        Member memberIn = tu.insertMember("annaTest");
        Integer id = memberIn.getMemberId();

        // Execution
        List<Member> listResult =  webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/members")
                        .queryParam("name", "annaTest")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .attribute("name", "annaTest")
                .exchange()
        // Assertion
                .expectStatus().isOk()  // 200
                .returnResult(Member.class)
                .getResponseBody()
                .toStream().toList();

        assertTrue(listResult.size() == 1);
        Member memberOut = listResult.get(0);
        assertEquals(memberIn, memberOut);
    }

    @Test
    public void findMemberByName_ERR002_MemberNotFound() {
        // Execution
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/members")
                        .queryParam("name", "$$$$$")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().value(status -> assertEquals("404", status.toString()))
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(response.getResponseBody());
                    assertTrue(body.contains(FaultInfo.NOT_FOUND_CODE));
                });
    }


    // ------------------------------------ findMembersByNameLike(nameLike) <- /api/members?nameLike= ------------------
    @Test
    public void findMembersByNameLike() {
        // Setup
        List<Member> listIn = new ArrayList<>(List.of(
                tu.insertMember("vinsenTest"),
                tu.insertMember("bearnTest"),
                tu.insertMember("tomTest"),
                tu.insertMember("redlTest")
        ));
        List<Member> listForCheck = listIn.stream().filter(member -> member.getName().contains("nTest")).toList();

        // Execution
        List<Member> listResult = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/members")
                        .queryParam("nameLike", "nTest")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().isOk()  // 200
                .returnResult(Member.class)
                .getResponseBody()
                .toStream().toList();

        Assertions.assertThat(listForCheck)
                  .containsExactlyInAnyOrderElementsOf(listResult);
    }

    @Test
    public void findMembersByNameLike_WhenEmptyList() {
        // Setup
        List<Member> listIn = new ArrayList<>(List.of(
                tu.insertMember("vinsenTest"),
                tu.insertMember("bearnTest"),
                tu.insertMember("tomTest"),
                tu.insertMember("redlTest")
        ));

        // Execution
        List<Member> listResult = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/members")
                        .queryParam("nameLike", "&&&&")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().isOk()  // 200
                .returnResult(Member.class)
                .getResponseBody()
                .toStream().toList();

        Assertions.assertThat(listResult).isEmpty();
    }

    @Test
    public void findMembersByNameLike_ERR001_BadRequest_400() {
        // Execution
        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/members")
                        .queryParam("nameLike", "&&&&")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
        // Assertion
                .expectStatus().value(status -> assertEquals("400", status.toString()))
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(response.getResponseBody());
                    assertTrue(body.contains(FaultInfo.UNEXPECTED_ERROR_CODE));
                });
    }

}
