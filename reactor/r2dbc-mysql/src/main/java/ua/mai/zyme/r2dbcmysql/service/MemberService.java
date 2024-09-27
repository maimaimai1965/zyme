package ua.mai.zyme.r2dbcmysql.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.exception.ServiceFault;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;


@RequiredArgsConstructor
@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    public Mono<Member> findByMemberId(Integer memberId) {
        return memberRepository.findById(memberId);
    }

    public Mono<Member> findByMemberIdWithFaultWhenNotExists(Integer memberId) {
        return findByMemberId(memberId)
                     .switchIfEmpty(Mono.error(new ServiceFault(AppFaultInfo.MEMBER_NOT_EXISTS, memberId)));
    }

}
