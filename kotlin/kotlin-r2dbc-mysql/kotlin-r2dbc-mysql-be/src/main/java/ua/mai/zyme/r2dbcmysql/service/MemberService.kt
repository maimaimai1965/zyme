package ua.mai.zyme.r2dbcmysql.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.mai.zyme.r2dbcmysql.entity.Member
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo
import ua.mai.zyme.r2dbcmysql.exception.FaultException
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository

@Service
class MemberService(

    private val memberRepository: MemberRepository

) {

    fun insertMember(member: Member): Mono<Member> {
        return if (member.memberId != null) {
            Mono.error(FaultException(AppFaultInfo.NEW_MEMBER_ID_MUST_BE_NULL))
        } else {
            memberRepository.save(member)
        }
    }

    fun insertMembers(fluxMembers: Flux<Member>): Flux<Member> {
        return fluxMembers.flatMap { insertMember(it) }
    }

    fun updateMember(member: Member): Mono<Member> {
        return if (member.memberId == null) {
            Mono.error(FaultException(AppFaultInfo.UPDATED_MEMBER_ID_MUST_NOT_BE_NULL))
        } else {
            memberRepository.save(member)
        }
    }

    fun findMemberByMemberId(memberId: Int): Mono<Member> {
        return memberRepository.findById(memberId)
    }

    fun findMemberByMemberIdWithFaultWhenNotExists(memberId: Int): Mono<Member> {
        return findMemberByMemberId(memberId)
            .switchIfEmpty(Mono.error(FaultException(AppFaultInfo.MEMBER_NOT_FOUND, memberId)))
    }
}