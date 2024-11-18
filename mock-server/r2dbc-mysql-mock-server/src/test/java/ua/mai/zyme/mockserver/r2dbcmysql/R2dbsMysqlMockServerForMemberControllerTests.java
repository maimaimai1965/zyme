package ua.mai.zyme.mockserver.r2dbcmysql;

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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.mai.zyme.mockserver.R2dbcMysqlApplicationTests;
import ua.mai.zyme.mockserver.r2dbcmysql.config.AppTestConfig;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.util.TestUtil;
import ua.mai.zyme.r2dbcmysql.webclient.R2dbsMysqlWebClient;
import ua.mai.zyme.r2dbcmysql.webclient.exception.AppClientError;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Для выполнения тестов должен быть запущен r2dbc-mysql-mock-server в Docker (см. mock-server/r2dbc-mysql-mock-server/docker/mock-server-compose.yaml)
 * */
@SpringBootTest
@ContextConfiguration(classes = {R2dbcMysqlApplicationTests.class})
@EnableR2dbcRepositories(basePackages = {"ua.mai.zyme.r2dbcmysql.repository"})
@Import({AppTestConfig.class})
@Slf4j
@ActiveProfiles(profiles = "test")
public class R2dbsMysqlMockServerForMemberControllerTests {

    @Autowired
    private R2dbsMysqlWebClient mysqlWebClient;


    // ------------------------------------ insertMember(monoMember) ---------------------------------------------------
    @Test
    public void insertMember() {
        // Setup
        Member memberIn = TestUtil.newMember("mikeTest");
        Member memberExpected = TestUtil.copyMember(TestUtil.newMember("mikeTest"), 900000);
        // Execution
        Member memberOut =  mysqlWebClient.insertMember(Mono.just(memberIn)).block();
        // Assertion
        assertEquals(memberOut, memberExpected);
    }

    @Test
    public void insertMember_ERR101_NotNullNewMemberId() {
        // Setup
        Member memberIn = TestUtil.copyMember(TestUtil.newMember("mikeTest"), -1);

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
        List<Member> listExpected = List.of(
                TestUtil.copyMember(memberIn1, 900001),
                TestUtil.copyMember(memberIn2, 900002));
        // Execution
        List<Member> listResult = mysqlWebClient.insertMembers(Flux.just(memberIn1, memberIn2)).toStream().toList();
        // Assertion
        Assertions.assertThat(listExpected).containsExactlyInAnyOrderElementsOf(listResult);
    }

//    @Test
//    public void insertMembers_ERR101_NotNullNewMemberId() {
//        // Setup
//        Member memberIn1 = TestUtil.newMember("mikeTest");
//        Member memberIn2 = TestUtil.newMember("rikTest");
//        memberIn2.setMemberId(-1);
//
//        AppClientError error = assertThrows(AppClientError.class, () -> {
//        // Execution
//            mysqlWebClient.insertMembers(Flux.just(memberIn1, memberIn2)).toStream().toList();
//        });
//        // Assertion
//        assertEquals("500", error.getClientFaultInfo().getStatus());
//        assertEquals(AppFaultInfo.NEW_MEMBER_ID_MUST_BE_NULL.code(), error.getClientFaultInfo().getErrorCd());
//    }


    // ------------------------------------ updateMember(monoMember) ---------------------------------------------------
    @Test
    public void updateMember() {
        // Setup
        Member memberExpected = TestUtil.copyMember(TestUtil.newMember("igorTEST"), 900005);
        // Execution
        Member memberOut =  mysqlWebClient.updateMember(Mono.just(memberExpected)).block();
        // Assertion
        assertEquals(memberOut, memberExpected);
    }


//    // ------------------------------------ deleteMemberById(id) -------------------------------------------------------
//    @Test
//    public void deleteMemberById() {
//        // Setup
//        Member memberIn = tu.insertMember("ivanTest");
//
//        // Execution
//        mysqlWebClient.deleteMemberById(memberIn.getMemberId()).block();
//
//        // Assertion
//        assertNull(tu.findMemberByMemberId(memberIn.getMemberId()));
//    }
//
//    @Test
//    public void deleteMemberById_WhenMemberNotExists() {
//        // Execution
//        mysqlWebClient.deleteMemberById(-1).block();
//    }
//
//
//    // ------------------------------------ findMembersAll() -----------------------------------------------------------
//    @Test
//    public void findMembersAll() {
//        // Setup
//        List<Member> listIn = new ArrayList<>(List.of(
//                tu.insertMember("aniTest"),
//                tu.insertMember("budiTest"),
//                tu.insertMember("cepTest"),
//                tu.insertMember("dodTest")
//        ));
//
//        // Execution
//        List<Member> listResult = mysqlWebClient.findMembersAll()
//                .toStream()
//                .filter(member -> TestUtil.isMemberForTest(member)) // Проверяем только на тестируемых данных
//                .toList();
//        // Assertion
//        Assertions.assertThat(listResult)
//                .containsExactlyInAnyOrderElementsOf(listIn);
//    }


    // ------------------------------------ findMemberByMemberId(memberId) ---------------------------------------------
    @Test
    public void findMemberByMemberId() {
        // Setup
        Member memberExpected = TestUtil.copyMember(TestUtil.newMember("mikeTest"), 900004);
        // Execution
        Member memberOut = mysqlWebClient.findMemberByMemberId(memberExpected.getMemberId()).block();
        // Assertion
        assertEquals(memberExpected, memberOut);
    }

//    @Test
//    public void findMemberByMemberId_ERR002_MemberNotFound() {
//        // Setup
//        Integer badMemberId = 1;
//
//        AppClientError error = assertThrows(AppClientError.class, () -> {
//            // Execution
//            mysqlWebClient.findMemberByMemberId(badMemberId).block();
//        });
//        // Assertion
//        assertEquals("404", error.getClientFaultInfo().getStatus());
//        assertEquals(AppFaultInfo.NOT_FOUND.code(), error.getClientFaultInfo().getErrorCd());
//    }
//
//
//    // ------------------------------------ findMemberByName(name) -----------------------------------------------------
//    @Test
//    public void findMemberByName() {
//        // Setup
//        Member memberIn = tu.insertMember("annaTest");
//
//        // Execution
//        Member memberOut = mysqlWebClient.findMemberByName(memberIn.getName()).block();
//        // Assertion
//        assertEquals(memberIn, memberOut);
//    }
//
//    @Test
//    public void findMemberByName_ERR002_MemberNotFound() {
//        // Setup
//        Member memberIn = tu.insertMember("annaTest");
//
//        AppClientError error = assertThrows(AppClientError.class, () -> {
//        // Execution
//            mysqlWebClient.findMemberByName("$$$$$").block();
//        });
//        // Assertion
//        assertEquals("404", error.getClientFaultInfo().getStatus());
//        assertEquals(AppFaultInfo.NOT_FOUND.code(), error.getClientFaultInfo().getErrorCd());
//    }
//
//
//    // ------------------------------------ findMembersByNameLike(nameLike) --------------------------------------------
//    @Test
//    public void findMembersByNameLike() {
//        // Setup
//        List<Member> listIn = new ArrayList<>(List.of(
//                tu.insertMember("vinsenTest"),
//                tu.insertMember("bearnTest"),
//                tu.insertMember("tomTest"),
//                tu.insertMember("redlTest")
//        ));
//        List<Member> listForCheck = listIn.stream().filter(member -> member.getName().contains("nTest")).toList();
//
//        // Execution
//        List<Member> listResult = mysqlWebClient.findMembersByNameLike("nTest").toStream().toList();
//        // Assertion
//        Assertions.assertThat(listForCheck)
//                  .containsExactlyInAnyOrderElementsOf(listResult);
//    }
//
//    @Test
//    public void findMembersByNameLike_WhenEmptyList() {
//        // Setup
//        List<Member> listIn = new ArrayList<>(List.of(
//                tu.insertMember("vinsenTest"),
//                tu.insertMember("bearnTest"),
//                tu.insertMember("tomTest"),
//                tu.insertMember("redlTest")
//        ));
//
//        // Execution
//        List<Member> listResult = mysqlWebClient.findMembersByNameLike("&&&&").toStream().toList();
//        // Assertion
//        Assertions.assertThat(listResult).isEmpty();
//    }

}
