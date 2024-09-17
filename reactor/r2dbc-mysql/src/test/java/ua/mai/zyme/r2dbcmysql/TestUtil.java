package ua.mai.zyme.r2dbcmysql;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.mapping.Column;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.repository.TransferRepository;

import java.time.LocalDateTime;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestUtil {

    private ConnectionFactory connectionFactory;
    private MemberRepository memberRepository;
    private TransferRepository transferRepository;

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public TransferRepository getTransferRepository() {
        return transferRepository;
    }
    public void setTransferRepository(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }


//    static public R2dbcEntityTemplate createR2dbcEntityTemplate(String dbUrl) {
//        ConnectionFactory connectionFactory = ConnectionFactories.get(dbUrl);
//        return new R2dbcEntityTemplate(connectionFactory);
//    }

    public R2dbcEntityTemplate createR2dbcEntityTemplate() {
        return createR2dbcEntityTemplate(connectionFactory);
    }
    static public R2dbcEntityTemplate createR2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }

    public void runSql(String sql) {
        runSql(connectionFactory, sql);
    }
    static public void runSql(ConnectionFactory connectionFactory, String sql) {
        runSql(createR2dbcEntityTemplate(connectionFactory), sql);
    }

    static public void runSql(R2dbcEntityTemplate template, String sql) {
        template.getDatabaseClient().sql(sql).fetch().rowsUpdated().block();
    }


    static public LocalDateTime now() {
        return LocalDateTime.now().withNano(0);
    }

    /* -- Methods used for Member testing ------------------------------------------------------------------------------*/

    static final private String sqlDeleteMembersTestData =
            "DELETE FROM member WHERE ucase(name) LIKE '%TEST%'";

    public void deleteMembersTestData() {
        deleteMembersTestData(connectionFactory);
    }
    static public void deleteMembersTestData(ConnectionFactory connectionFactory) {
        runSql(connectionFactory, sqlDeleteMembersTestData);
    }

    static public boolean isMemberForTest(Member member) {
        return member.getName().toUpperCase().contains("TEST");
    }

    static public Member newMember(String name) {
        Member member = Member.builder().name(name).build();
        assertTrue(isMemberForTest(member));
        return member;
    }

    public Member insertMember(Member member) {
        return insertMember(memberRepository, member);
    }
    static public Member insertMember(MemberRepository memberRepository, Member member) {
        assertTrue(isMemberForTest(member));
        assertNull(member.getMemberId());  // При вставке нового member идентификатор не известен - он назначается в БД.
        return memberRepository.save(member).block();
    }

    public Member insertMember(String name) {
        return insertMember(memberRepository, name);
    }
    static public Member insertMember(MemberRepository memberRepository, String name) {
        return insertMember(memberRepository, newMember(name));
    }

    public void deleteMember(Integer memberId) {
        deleteMember(memberRepository, memberId);
    }
    static public void deleteMember(MemberRepository memberRepository, Integer memberId) {
        memberRepository.deleteById(memberId).block();
    }

    public void deleteMember(Member member) {
        deleteMember(memberRepository, member);
    }
    static public void deleteMember(MemberRepository memberRepository, Member member) {
        assertTrue(isMemberForTest(member));
        memberRepository.deleteById(member.getMemberId()).block();
    }

    public Member findMemberById(Integer memberId) {
        return findMemberById(memberRepository, memberId);
    }
    static public Member findMemberById(MemberRepository memberRepository, Integer memberId) {
        return memberRepository.findById(memberId).block();
    }


    /* -- Methods used for Transfer testing ------------------------------------------------------------------------------*/

    static final private String sqlDeleteTransfersTestData =
        """
            DELETE FROM transfer t
            WHERE t.to_member_id IN (SELECT member_id FROM member WHERE ucase(name) LIKE '%TEST%')
               OR t.from_member_id IN (SELECT member_id FROM member WHERE ucase(name) LIKE '%TEST%')
        """;

    public void deleteTransfersTestData() {
        deleteTransfersTestData(connectionFactory);
    }
    static public void deleteTransfersTestData(ConnectionFactory connectionFactory) {
        runSql(connectionFactory, sqlDeleteTransfersTestData);
    }

    static public Transfer newTransfer(Long amount, Integer toMemberId, Integer fromMemberId) {
        return newTransfer(amount, toMemberId, fromMemberId, now());
    }

    static public Transfer newTransfer(Long amount, Integer toMemberId, Integer fromMemberId, LocalDateTime createdDate) {
        Transfer transfer = Transfer.builder()
                .amount(amount)
                .toMemberId(toMemberId)
                .fromMemberId(fromMemberId)
                .createdDate(createdDate).build();
        return transfer;
    }

    public Transfer insertTransfer(Transfer transfer) {
        return insertTransfer(transferRepository, transfer);
    }
    static public Transfer insertTransfer(TransferRepository transferRepository, Transfer transfer) {
        assertNull(transfer.getTransferId());  // При вставке нового Transfer идентификатор не известен - он назначается в БД.
        return transferRepository.save(transfer).block();
    }

    public Transfer insertTransfer(Long amount, Integer toMemberId, Integer fromMemberId) {
        return insertTransfer(transferRepository, amount, toMemberId, fromMemberId);
    }
    static public Transfer insertTransfer(TransferRepository transferRepository,
                                          Long amount, Integer toMemberId, Integer fromMemberId) {
        return insertTransfer(transferRepository, newTransfer(amount, toMemberId, fromMemberId, now()));
    }

    public Transfer insertTransfer(Long amount, Integer toMemberId, Integer fromMemberId, LocalDateTime createdDate) {
        return insertTransfer(transferRepository, amount, toMemberId, fromMemberId, createdDate);
    }
    static public Transfer insertTransfer(TransferRepository transferRepository,
                                          Long amount, Integer toMemberId, Integer fromMemberId, LocalDateTime createdDate) {
        return insertTransfer(transferRepository, newTransfer(amount, toMemberId, fromMemberId, createdDate));
    }

    public void deleteTransfer(Long transferId) {
        deleteTransfer(transferRepository, transferId);
    }
    static public void deleteTransfer(TransferRepository transferRepository, Long transferId) {
        transferRepository.deleteById(transferId).block();
    }

    public void deleteTransfer(Transfer transfer) {
        deleteTransfer(transferRepository, transfer);
    }
    static public void deleteTransfer(TransferRepository transferRepository, Transfer transfer) {
        transferRepository.deleteById(transfer.getTransferId()).block();
    }

    public Transfer findTransferById(Long transferId) {
        return findTransferById(transferRepository, transferId);
    }
    static public Transfer findTransferById(TransferRepository transferRepository, Long transferId) {
        return transferRepository.findById(transferId).block();
    }

}
