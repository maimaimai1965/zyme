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
import reactor.test.StepVerifier;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.exception.FaultException;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.service.MemberService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.*;


@SpringBootTest
@SpringJUnitConfig
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


    @Test
    public void insertMember() {
        // Setup
        Member memberIn = TestUtil.newMember("annaTest");
        List<Member> listResult = new ArrayList<>(1);

        // Execution
        StepVerifier.create(
                memberService.insertMember(memberIn))
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
                    .consumeNextWith(result -> listResult.add(result))
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
                    .consumeNextWith(result -> listResult.add(result))
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
