package ua.mai.zyme.r2dbcmysql.controller

import org.springframework.web.bind.annotation.*
import ua.mai.zyme.r2dbcmysql.entity.Member
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo
import ua.mai.zyme.r2dbcmysql.exception.FaultException
import ua.mai.zyme.r2dbcmysql.exception.FaultInfo
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository
import ua.mai.zyme.r2dbcmysql.service.MemberService
import org.apache.commons.lang3.RandomStringUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/members")
class MemberController(
    private val memberRepository: MemberRepository,
    private val memberService: MemberService
) {

    @PostMapping
    fun insertMember(@RequestBody member: Member): Mono<Member> {
        return memberService.insertMember(member)
    }

    @PostMapping("/flux")
    fun insertMembers(@RequestBody fluxMember: Flux<Member>): Flux<Member> {
        return memberService.insertMembers(fluxMember)
    }

    private fun generateRandomMembers(number: Int): Flux<Member> {
        return Mono.fromSupplier {
            Member(null, RandomStringUtils.randomAlphabetic(5))
        }.flatMap(memberService::insertMember)
            .repeat(number.toLong())
    }

    @PutMapping
    fun updateMember(@RequestBody member: Member): Mono<Member> {
        return memberRepository.findById(member.memberId!!)
            .flatMap { memberService.updateMember(member) }
    }

    @DeleteMapping("/{id}")
    fun deleteMemberById(@PathVariable id: Int): Mono<Void> {
        return memberRepository.deleteById(id)
    }

    @GetMapping
    fun findAll(): Flux<Member> {
        return memberRepository.findAll()
    }

    @GetMapping("/{id}")
    fun findMemberByMemberId(@PathVariable id: Int): Mono<Member> {
        return memberService.findMemberByMemberId(id)
            .switchIfEmpty(Mono.error(
                FaultException(
                    AppFaultInfo.NOT_FOUND,
                    FaultInfo.createParamFor_NOT_FOUND("Member", "memberId", id.toString())
                )
            ))
    }

    @GetMapping(params = ["name"])
    fun findMemberByName(@RequestParam name: String): Mono<Member> {
        return memberRepository.findByName(name)
            .switchIfEmpty(Mono.error(
                FaultException(
                    AppFaultInfo.NOT_FOUND,
                    FaultInfo.createParamFor_NOT_FOUND("Member", "memberName", name)
                )
            ))
    }

    @GetMapping(params = ["nameLike"])
    fun findMembersByNameLike(@RequestParam nameLike: String): Flux<Member> {
        return memberRepository.findByNameLike("%$nameLike%")
    }

}