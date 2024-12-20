package ua.mai.zyme.r2dbcmysql.service;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;
import ua.mai.zyme.r2dbcmysql.R2dbcMysqlApplicationTests;
import ua.mai.zyme.r2dbcmysql.config.AppTestConfig;
import reactor.test.StepVerifier;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.exception.FaultException;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.util.TestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@ContextConfiguration(classes = {R2dbcMysqlApplicationTests.class})
@EnableR2dbcRepositories(basePackages = {"ua.mai.zyme.r2dbcmysql.repository"})
@ComponentScan(basePackages = {"ua.mai.zyme.r2dbcmysql.service"})
@Import(AppTestConfig.class)
@Slf4j
@ActiveProfiles(profiles = "test")
class MemberServiceTest {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

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


    // ------------------------------------ insertMember(member) -------------------------------------------------------

    @Test
    public void insertMember() {
        // Setup
        Member memberIn = TestUtil.newMember("annaTest");
        List<Member> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                memberService.insertMember(memberIn))
        // Assertion
                    .recordWith(() -> listResult)
                    .expectNextCount(1)
                    .verifyComplete();

        Member memberOut = listResult.get(0);
        assertNotNull(memberOut.getMemberId());
        Member member_Db = tu.findMemberByMemberId(memberOut.getMemberId());
        Assertions.assertThat(member_Db)
                .usingRecursiveComparison()
                .isEqualTo(memberOut);
    }

    @Test
    public void insertMember_Fault_WhenNewMemberHasMemberId() {
        // Setup
        Member memberIn = TestUtil.newMember("annaTest");
        memberIn.setMemberId(-1);

        // Execution
        StepVerifier.create(
                memberService.insertMember(memberIn))
        // Assertion
                    .consumeErrorWith(error -> {
                         assertThat(error).isInstanceOf(FaultException.class);
                         FaultException fault = (FaultException) error;
                         assertThat(fault.getCode()).isEqualTo(AppFaultInfo.NEW_MEMBER_ID_MUST_BE_NULL.code());
                     })
                    .verify();
    }


    // ------------------------------------ insertMembers(fluxMember) --------------------------------------------------
    @Test
    public void insertMembers() {
        // Setup
        Member memberIn1 = TestUtil.newMember("mikeTest");
        Member memberIn2 = TestUtil.newMember("rikTest");
        List<Member> listResult = new ArrayList<>(2);

        // Execution
        StepVerifier.create(
                        memberService.insertMembers(Flux.just(memberIn1, memberIn2)))
        // Assertion
                    .recordWith(() -> listResult)
                    .expectNextCount(2)
                    .verifyComplete();

        assertTrue(listResult.size() == 2);

        Member memberOut1 = listResult.get(0);
        Member memberOut2 = listResult.get(1);
        List<Member> list_Db = List.of(tu.findMemberByMemberId(memberOut1.getMemberId()),
                                       tu.findMemberByMemberId(memberOut2.getMemberId()));
        Assertions.assertThat(listResult).containsExactlyInAnyOrderElementsOf(list_Db);
    }

    @Test
    public void insertMembers_Fault_WhenNewMemberHasMemberId() {
        // Setup
        Member memberIn1 = TestUtil.newMember("mikeTest");
        Member memberIn2 = TestUtil.newMember("rikTest");
        memberIn2.setMemberId(-1);

        // Execution
        StepVerifier.create(
                memberService.insertMembers(Flux.just(memberIn1, memberIn2)))
        // Assertion
                    .consumeErrorWith(error -> {
                        assertThat(error).isInstanceOf(FaultException.class);
                        FaultException fault = (FaultException) error;
                        assertThat(fault.getCode()).isEqualTo(AppFaultInfo.NEW_MEMBER_ID_MUST_BE_NULL.code());
                     })
                    .verify();
    }


    // ------------------------------------ updateMember(member) -------------------------------------------------------
    @Test
    public void updateMember() {
        // Setup
        Member memberIn = tu.insertMember("billTest");
        memberIn.setName("bill2Test");
        List<Member> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                memberService.updateMember(memberIn))
        // Assertion
                    .recordWith(() -> listResult)
                    .expectNextCount(1)
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
    public void updateMember_Fault_WhenUpdatedMemberDasNotHaveMemberId() {
        // Setup
        Member memberIn = TestUtil.newMember("annaTest");

        // Execution
        StepVerifier.create(
                        memberService.updateMember(memberIn))
        // Assertion
                    .consumeErrorWith(error -> {
                         assertThat(error).isInstanceOf(FaultException.class);
                         FaultException fault = (FaultException) error;
                         assertThat(fault.getCode()).isEqualTo(AppFaultInfo.UPDATED_MEMBER_ID_MUST_NOT_BE_NULL.code());
                     })
                    .verify();
    }

    // ------------------------------------ deleteMemberById(id) -------------------------------------------------------


    // ------------------------------------ findMemberByMemberId(memberId) ---------------------------------------------
    @Test
    public void findMemberByMemberId() {
        // Setup
        Member memberIn = tu.insertMember("annaTest");
        Member memberIn2 = tu.insertMember("anna2Test");
        List<Member> listResult = new ArrayList<>();

        // Execution
        StepVerifier.create(
                memberService.findMemberByMemberId(memberIn.getMemberId()))
        // Assertion
                    .recordWith(() -> listResult)
                    .expectNextCount(1)
                    .verifyComplete();

        Member memberOut = listResult.get(0);
        Assertions.assertThat(memberIn.getMemberId()).isEqualTo(memberOut.getMemberId());
    }

    @Test
    public void findMemberByMemberId_WhenMemberNotExists() {
        // Setup
        Integer noExistedMemberId = -1;

        // Execution
        StepVerifier.create(
                        memberService.findMemberByMemberId(noExistedMemberId))
        // Assertion
                    .verifyComplete(); // Проверяет получение Mono с пустым значением.
    }

    @Test
    public void findMemberByMemberIdWithFaultWhenNotExists() {
        // Setup
        Member memberIn = tu.insertMember("annaTest");
        Member memberIn2 = tu.insertMember("anna2Test");
        List<Member> listResult = new ArrayList<>();

        // Execution
        StepVerifier.create(
                memberService.findMemberByMemberIdWithFaultWhenNotExists(memberIn.getMemberId()))
        // Assertion
                    .recordWith(() -> listResult)
                    .expectNextCount(1)
                    .verifyComplete();

        Member memberOut = listResult.get(0);
        Assertions.assertThat(memberIn.getMemberId()).isEqualTo(memberOut.getMemberId());
    }

    @Test
    public void findMemberByMemberIdWithFaultWhenNotExists_Fault_MemberNotExists() {
        // Setup
        Integer noExistedMemberId = -1;

        // Execution
        StepVerifier.create(
                memberService.findMemberByMemberIdWithFaultWhenNotExists(noExistedMemberId))
        // Assertion
                    .consumeErrorWith(error -> {
                         assertThat(error).isInstanceOf(FaultException.class);
                         FaultException fault = (FaultException) error;
                         assertThat(fault.getCode()).isEqualTo(AppFaultInfo.MEMBER_NOT_FOUND.code());
                         assertThat(fault.getErrorParameters().get(0)).isEqualTo(noExistedMemberId);
                     })
                    .verify();
    }

}
