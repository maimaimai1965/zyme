package ua.mai.zyme.r2dbcmysql.webclient;

import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import ua.mai.zyme.r2dbcmysql.webclient.exception.AppClientError;
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
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.R2dbcMysqlApplicationTests;
import ua.mai.zyme.r2dbcmysql.config.AppTestConfig;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.util.TestUtil;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@ContextConfiguration(classes = {R2dbcMysqlApplicationTests.class})
@EnableR2dbcRepositories(basePackages = {"ua.mai.zyme.r2dbcmysql.repository"})
@Import({AppTestConfig.class})
@Slf4j
@ActiveProfiles(profiles = "test")
public class R2dbsMysqlWebClientForMemberControllerTests {

    @Autowired
    private R2dbsMysqlWebClient mysqlWebClient;

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
    }

    @AfterEach
    public void cleanup() {
        // Teardown
        tu.deleteMembersTestData();
    }


    // ------------------------------------ insertMember(monoMember) ---------------------------------------------------
    @Test
    public void insertMember() {
        // Setup
        Member memberIn = TestUtil.newMember("mikeTest");

        // Execution
        Member memberOut =  mysqlWebClient.insertMember(Mono.just(memberIn)).block();

        // Assertion
        assertNotNull(memberOut);
        Member member_Db = tu.findMemberByMemberId(memberOut.getMemberId());
        assertEquals(memberOut, member_Db);
    }

    @Test
    public void insertMember_ERR101_NotNullNewMemberId() {
        // Setup
        Member memberIn = TestUtil.newMember("mikeTest");
        memberIn.setMemberId(-1);

        AppClientError error = assertThrows(AppClientError.class, () -> {
        // Execution
            mysqlWebClient.insertMember(Mono.just(memberIn)).block();
        });
        // Assertion
        assertEquals("500", error.getClientFaultInfo().getStatus());
        assertEquals(AppFaultInfo.NEW_MEMBER_ID_MUST_BE_NULL.code(), error.getClientFaultInfo().getErrorCd());
    }


    // ------------------------------------ insertMembers(fluxMember) --------------------------------------------------
    @Test
    public void insertMembers() {
        // Setup
        Member memberIn1 = TestUtil.newMember("mikeTest");
        Member memberIn2 = TestUtil.newMember("rikTest");

        // Execution
        List<Member> listResult = mysqlWebClient.insertMembers(Flux.just(memberIn1, memberIn2)).toStream().toList();

        assertTrue(listResult.size() == 2);

        int memberId1 = listResult.get(0).getMemberId();
        int memberId2 = listResult.get(1).getMemberId();
        List<Member> list_Db = List.of(tu.findMemberByMemberId(memberId1),
                                       tu.findMemberByMemberId(memberId2));
        Assertions.assertThat(listResult).containsExactlyInAnyOrderElementsOf(list_Db);
    }

    @Test
    public void insertMembers_ERR101_NotNullNewMemberId() {
        // Setup
        Member memberIn1 = TestUtil.newMember("mikeTest");
        Member memberIn2 = TestUtil.newMember("rikTest");
        memberIn2.setMemberId(-1);

        AppClientError error = assertThrows(AppClientError.class, () -> {
        // Execution
            mysqlWebClient.insertMembers(Flux.just(memberIn1, memberIn2)).toStream().toList();
        });
        // Assertion
        assertEquals("500", error.getClientFaultInfo().getStatus());
        assertEquals(AppFaultInfo.NEW_MEMBER_ID_MUST_BE_NULL.code(), error.getClientFaultInfo().getErrorCd());
    }


    // ------------------------------------ updateMember(monoMember) ---------------------------------------------------
    @Test
    public void updateMember() {
        // Setup
        Member memberIn = tu.insertMember("ivanTest");
        memberIn.setName("igorTEST");

        // Execution
        Member memberOut =  mysqlWebClient.updateMember(Mono.just(memberIn)).block();

        // Assertion
        assertEquals(memberIn, memberOut);
        Member member_Db = tu.findMemberByMemberId(memberOut.getMemberId());
        assertEquals(memberIn, member_Db);
    }


    // ------------------------------------ deleteMemberById(id) -------------------------------------------------------
    @Test
    public void deleteMemberById() {
        // Setup
        Member memberIn = tu.insertMember("ivanTest");

        // Execution
        mysqlWebClient.deleteMemberById(memberIn.getMemberId()).block();

        // Assertion
        assertNull(tu.findMemberByMemberId(memberIn.getMemberId()));
    }

    @Test
    public void deleteMemberById_WhenMemberNotExists() {
        // Execution
        mysqlWebClient.deleteMemberById(-1).block();
    }


    // ------------------------------------ findMembersAll() -----------------------------------------------------------
    @Test
    public void findMembersAll() {
        // Setup
        List<Member> listIn = new ArrayList<>(List.of(
                tu.insertMember("aniTest"),
                tu.insertMember("budiTest"),
                tu.insertMember("cepTest"),
                tu.insertMember("dodTest")
        ));

        // Execution
        List<Member> listResult = mysqlWebClient.findMembersAll()
                .toStream()
                .filter(member -> TestUtil.isMemberForTest(member)) // Проверяем только на тестируемых данных
                .toList();
        // Assertion
        Assertions.assertThat(listResult)
                .containsExactlyInAnyOrderElementsOf(listIn);
    }


    // ------------------------------------ findMemberByMemberId(id) ---------------------------------------------------
    @Test
    public void findMemberByMemberId() {
        // Setup
        Member memberIn = tu.insertMember("annaTest");
        Integer id = memberIn.getMemberId();

        // Execution
        Member memberOut = mysqlWebClient.findMemberByMemberId(memberIn.getMemberId()).block();
        // Assertion
        assertEquals(memberIn, memberOut);
    }

    @Test
    public void findMemberByMemberId_ERR002_MemberNotFound() {
        // Setup
        Integer badMemberId = 1;

        AppClientError error = assertThrows(AppClientError.class, () -> {
            // Execution
            mysqlWebClient.findMemberByMemberId(badMemberId).block();
        });
        // Assertion
        assertEquals("404", error.getClientFaultInfo().getStatus());
        assertEquals(AppFaultInfo.NOT_FOUND.code(), error.getClientFaultInfo().getErrorCd());
    }


    // ------------------------------------ findMemberByName(name) -----------------------------------------------------
    @Test
    public void findMemberByName() {
        // Setup
        Member memberIn = tu.insertMember("annaTest");

        // Execution
        Member memberOut = mysqlWebClient.findMemberByName(memberIn.getName()).block();
        // Assertion
        assertEquals(memberIn, memberOut);
    }

    @Test
    public void findMemberByName_ERR002_MemberNotFound() {
        // Setup
        Member memberIn = tu.insertMember("annaTest");

        AppClientError error = assertThrows(AppClientError.class, () -> {
        // Execution
            mysqlWebClient.findMemberByName("$$$$$").block();
        });
        // Assertion
        assertEquals("404", error.getClientFaultInfo().getStatus());
        assertEquals(AppFaultInfo.NOT_FOUND.code(), error.getClientFaultInfo().getErrorCd());
    }


    // ------------------------------------ findMembersByNameLike(nameLike) --------------------------------------------
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
        List<Member> listResult = mysqlWebClient.findMembersByNameLike("nTest").toStream().toList();
        // Assertion
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
        List<Member> listResult = mysqlWebClient.findMembersByNameLike("&&&&").toStream().toList();
        // Assertion
        Assertions.assertThat(listResult).isEmpty();
    }

}
