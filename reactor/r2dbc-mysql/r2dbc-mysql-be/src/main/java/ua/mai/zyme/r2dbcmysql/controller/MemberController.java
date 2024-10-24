package ua.mai.zyme.r2dbcmysql.controller;

import org.springframework.web.bind.annotation.*;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.exception.FaultException;
import ua.mai.zyme.r2dbcmysql.exception.FaultInfo;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.service.MemberService;

@RestController
@RequestMapping(value = "/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @PostMapping()
    public Mono<Member> insertMember(@RequestBody Member member) {
        return memberService.insertMember(member);
    }

    @PostMapping("/flux")
    public Flux<Member> insertMembers(@RequestBody Flux<Member> fluxMember) {
        return memberService.insertMembers(fluxMember);
    }

//  @PostMapping(value = "/{number}")
//  public Flux<Member> insertRandomMembers(@PathVariable int number) {
//    return generateRandomMember(number).subscribeOn(Schedulers.boundedElastic());
//  }

    private Flux<Member> generateRandomMembers(int number) {
        return Mono.fromSupplier(
                        () -> Member.builder().name(RandomStringUtils.randomAlphabetic(5)).build())
                .flatMap(memberService::insertMember)
                .repeat(number);
    }

    @PutMapping
    public Mono<Member> updateMember(@RequestBody Member member) {
        return memberRepository
                .findById(member.getMemberId())
                .flatMap(memberResult -> memberService.updateMember(member));
    }

    @DeleteMapping(value = "/{id}")
    public Mono<Void> deleteMemberById(@PathVariable Integer id) {
        return memberRepository.deleteById(id);
    }

    @GetMapping(value = "")
    public Flux<Member> findAll() {
        return memberRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Mono<Member> findMemberByMemberId(@PathVariable Integer id) {
        return memberService.findMemberByMemberId(id)
                .switchIfEmpty(Mono.error(
                        new FaultException(AppFaultInfo.NOT_FOUND,
                                FaultInfo.createParamFor_NOT_FOUND("Member", "memberId", id.toString()))));
    }

    @GetMapping(value = "", params = "name")
    public Mono<Member> findMemberByName(@RequestParam String name) {
        return memberRepository.findByName(name)
                .switchIfEmpty(Mono.error(
                        new FaultException(AppFaultInfo.NOT_FOUND,
                                FaultInfo.createParamFor_NOT_FOUND("Member", "memberName", name))));
    }

    @GetMapping(value = "", params = "nameLike")
    public Flux<Member> findMembersByNameLike(@RequestParam String nameLike) {
        return memberRepository.findByNameLike("%" + nameLike + "%");
    }

}
