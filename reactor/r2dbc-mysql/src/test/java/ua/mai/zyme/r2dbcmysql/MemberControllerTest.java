package ua.mai.zyme.r2dbcmysql;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.exception.FaultInfo;
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
import ua.mai.zyme.r2dbcmysql.util.TestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@SpringBootTest
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
        List<Member> list =  webTestClient
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

        assertTrue(list.size() == 1);
        Member memberOut = list.get(0);
        Member member_Db = tu.findMemberByMemberId(memberOut.getMemberId());
        assertEquals(memberOut, member_Db);
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


    // ------------------------------------ findMemberByMemberId(id) <- /api/members/{id} ------------------------------

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
        // Setup

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
        // Setup

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
        // Setup

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
        // Setup

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
