package ua.mai.zyme.r2dbcmysql.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.exception.FaultException;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;


@RequiredArgsConstructor
@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    public Mono<Member> insertMember(Member member) {
        if (member.getMemberId() != null) {
            return Mono.error(new FaultException(AppFaultInfo.NEW_MEMBER_ID_MUST_BE_NULL));
        }
        return memberRepository.save(member);
    }

    public Flux<Member> insertMembers(Flux<Member> fluxMembers) {
        Flux<Member> result = fluxMembers.flatMap(this::insertMember);
        return result;
    }

    public Mono<Member> updateMember(Member member) {
        if (member.getMemberId() == null) {
            return Mono.error(new FaultException(AppFaultInfo.UPDATED_MEMBER_ID_MUST_NOT_BE_NULL));
        }
        return memberRepository.save(member);
    }

    public Mono<Member> findMemberByMemberId(Integer memberId) {
        return memberRepository.findById(memberId);
    }

    public Mono<Member> findMemberByMemberIdWithFaultWhenNotExists(Integer memberId) {
        return findMemberByMemberId(memberId)
                     .switchIfEmpty(Mono.error(new FaultException(AppFaultInfo.MEMBER_NOT_FOUND, memberId)));
    }

}
