package ua.mai.zyme.r2dbcmysql.util;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import ua.mai.zyme.r2dbcmysql.entity.Balance;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.entity.Transfer;
import ua.mai.zyme.r2dbcmysql.repository.BalanceRepository;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import ua.mai.zyme.r2dbcmysql.repository.TransferRepository;

import java.time.LocalDateTime;

public class TestUtil {

    private ConnectionFactory connectionFactory;
    private MemberRepository memberRepository;
    private BalanceRepository balanceRepository;
    private TransferRepository transferRepository;

    private static final String ASSERTION_MESSAGE_NAME_NOT_FOR_TEST = "Member name is not for test!";
    private static final String ASSERTION_MESSAGE_FOR_NEW_MEMBER_ID_MUST_BE_NULL = "For a new member the memberId must be null";
    private static final String ASSERTION_MESSAGE_FOR_NEW_TRANSFER_ID_MUST_BE_NULL = "For a new transfer the transferId must be null";

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

    public BalanceRepository getBalanceRepository() {
        return balanceRepository;
    }
    public void setBalanceRepository(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
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
        return AppUtil.now();
    }
    static public LocalDateTime roundLocalDateTime(LocalDateTime dateTime) {
        return AppUtil.roundLocalDateTime(dateTime);
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
        Member member = new Member(null, name);
        assertTrue(isMemberForTest(member), ASSERTION_MESSAGE_NAME_NOT_FOR_TEST);
        return member;
    }

    public Member insertMember(Member member) {
        return insertMember(memberRepository, member);
    }
    static public Member insertMember(MemberRepository memberRepository, Member member) {
        assertTrue(isMemberForTest(member), ASSERTION_MESSAGE_NAME_NOT_FOR_TEST);
        assertNull(member.getMemberId(), ASSERTION_MESSAGE_FOR_NEW_MEMBER_ID_MUST_BE_NULL); // При вставке нового member идентификатор не известен - он назначается в БД.
        return memberRepository.save(member).block();
    }

    public Member insertMember(String name) {
        return insertMember(memberRepository, name);
    }
    static public Member insertMember(MemberRepository memberRepository, String name) {
        return insertMember(memberRepository, newMember(name));
    }

    public Member insertMemberWithBalance(String name, Long amount, LocalDateTime dateTime) {
        return insertMemberWithBalance(memberRepository, balanceRepository, name, amount, dateTime);
    }
    static public Member insertMemberWithBalance(MemberRepository memberRepository, BalanceRepository balanceRepository,
                                                 String name, Long amount, LocalDateTime dateTime) {
        Member member = insertMember(memberRepository, name);
        Balance balanceToIn = insertBalance(balanceRepository,
                                            new Balance(member.getMemberId(), amount, dateTime, dateTime));
        return member;
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
        assertTrue(isMemberForTest(member), ASSERTION_MESSAGE_NAME_NOT_FOR_TEST);
        memberRepository.deleteById(member.getMemberId()).block();
    }

    public Member findMemberByMemberId(Integer memberId) {
        return findMemberByMemberId(memberRepository, memberId);
    }
    static public Member findMemberByMemberId(MemberRepository memberRepository, Integer memberId) {
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

    static public Transfer newTransfer(Integer fromMemberId, Integer toMemberId, Long amount) {
        return newTransfer(fromMemberId, toMemberId,amount, now());
    }

    static public Transfer newTransfer(Integer fromMemberId, Integer toMemberId, Long amount, LocalDateTime createdDate) {
        Transfer transfer =
                new Transfer(null, fromMemberId, toMemberId, amount, createdDate);
        return transfer;
    }

    public Transfer insertTransfer(Transfer transfer) {
        return insertTransfer(transferRepository, transfer);
    }
    static public Transfer insertTransfer(TransferRepository transferRepository, Transfer transfer) {
        assertNull(transfer.getTransferId(), ASSERTION_MESSAGE_FOR_NEW_TRANSFER_ID_MUST_BE_NULL); // При вставке нового Transfer идентификатор не известен - он назначается в БД.
        return transferRepository.save(transfer).block();
    }

    public Transfer insertTransfer(Integer fromMemberId, Integer toMemberId, Long amount) {
        return insertTransfer(transferRepository, fromMemberId, toMemberId, amount);
    }
    static public Transfer insertTransfer(TransferRepository transferRepository,
                                          Integer fromMemberId, Integer toMemberId, Long amount) {
        return insertTransfer(transferRepository, newTransfer(fromMemberId, toMemberId, amount, now()));
    }

    public Transfer insertTransfer(Integer fromMemberId, Integer toMemberId, Long amount, LocalDateTime createdDate) {
        return insertTransfer(transferRepository, fromMemberId, toMemberId, amount, createdDate);
    }
    static public Transfer insertTransfer(TransferRepository transferRepository,
                                          Integer fromMemberId, Integer toMemberId, Long amount, LocalDateTime createdDate) {
        return insertTransfer(transferRepository, newTransfer(fromMemberId, toMemberId, amount, createdDate));
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

    public Transfer findTransferByTransferId(Long transferId) {
        return findTransferByTransferId(transferRepository, transferId);
    }
    static public Transfer findTransferByTransferId(TransferRepository transferRepository, Long transferId) {
        return transferRepository.findById(transferId).block();
    }


    /* -- Methods used for Balance testing ------------------------------------------------------------------------------*/

    static final private String sqlDeleteBalancesTestData =
            """
                DELETE FROM balance b
                WHERE b.member_id IN (SELECT member_id FROM member WHERE ucase(name) LIKE '%TEST%')
            """;

    public void deleteBalancesTestData() {
        deleteBalancesTestData(connectionFactory);
    }
    static public void deleteBalancesTestData(ConnectionFactory connectionFactory) {
        runSql(connectionFactory, sqlDeleteBalancesTestData);
    }

    static public Balance newBalance(Integer memberId, Long amount, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        Balance balance = new Balance(memberId, amount, createdDate, lastModifiedDate);
        return balance;
    }

    public Balance insertBalance(Balance balance) {
        return insertBalance(balanceRepository, balance);
    }
    static public Balance insertBalance(BalanceRepository balanceRepository, Balance balance) {
        return insertBalance(balanceRepository,
                balance.getMemberId(), balance.getAmount(), balance.getCreatedDate(), balance.getLastModifiedDate());
    }
    public Balance insertBalance(Integer memberId, Long amount, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        return insertBalance(balanceRepository, memberId, amount, createdDate, lastModifiedDate);
    }
    static public Balance insertBalance(BalanceRepository balanceRepository,
                                        Integer memberId, Long amount, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        balanceRepository.insert(memberId, amount, createdDate, lastModifiedDate).block();
        return newBalance(memberId, amount, createdDate, lastModifiedDate);
    }

    public void deleteBalance(Integer memberId) {
        deleteBalance(balanceRepository, memberId);
    }
    static public void deleteBalance(BalanceRepository balanceRepository, Integer memberId) {
        balanceRepository.deleteById(memberId).block();
    }

    public void deleteBalance(Balance balance) {
        deleteBalance(balanceRepository, balance);
    }
    static public void deleteBalance(BalanceRepository balanceRepository, Balance balance) {
        balanceRepository.deleteById(balance.getMemberId()).block();
    }

    public Balance findBalanceByMemberId(Integer memberId) {
        return findBalanceByMemberId(balanceRepository, memberId);
    }
    static public Balance findBalanceByMemberId(BalanceRepository balanceRepository, Integer memberId) {
        return balanceRepository.findById(memberId).block();
    }


    public static class SimpleAssertionError extends RuntimeException {

       public SimpleAssertionError(String message) {
           super(message);
       }

    }

    private static void assertTrue(boolean condition, String errorMessage) {
        if (!condition)
            throw new SimpleAssertionError(errorMessage);
    }

    private static void assertNull(Object obj, String errorMessage) {
        if (obj != null)
            throw new SimpleAssertionError(errorMessage);
    }

}
