package ua.mai.zyme.r2dbcmysql.controller;

import org.springframework.web.bind.annotation.*;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import ua.mai.zyme.r2dbcmysql.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping(value = "/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

  private final MemberRepository memberRepository;

  @PostMapping()
  public Mono<Member> insertMember(@RequestBody Member member) {
    return memberRepository.save(member);
  }

  @PostMapping(value = "/{number}")
  public Flux<Member> insertRandomMembers(@PathVariable int number) {
    return generateRandomMember(number).subscribeOn(Schedulers.boundedElastic());
  }

  private Flux<Member> generateRandomMember(int number) {
    return Mono.fromSupplier(
            () -> Member.builder().name(RandomStringUtils.randomAlphabetic(5)).build())
        .flatMap(memberRepository::save)
        .repeat(number);
  }

  @PutMapping
  public Mono<Member> updateMember(@RequestBody Member member) {
    return memberRepository
        .findById(member.getMemberId())
        .flatMap(memberResult -> memberRepository.save(member));
  }

  @DeleteMapping(value = "/{id}")
  public Mono<Void> deleteMemberById(@PathVariable Integer id) {
    return memberRepository.deleteById(id);
  }

//  @DeleteMapping
//  public Mono<Void> deleteMember(@RequestBody Member member) {
//    return memberRepository.deleteById(member.getId());
//  }

  @GetMapping(value = "")
  public Flux<Member> findAll() {
    return memberRepository.findAll();
  }

  @GetMapping(value = "/{id}")
  public Mono<Member> findById(@PathVariable Integer id) {
    return memberRepository.findById(id);
  }

  @GetMapping(value = "", params = "name")
  public Mono<Member> findByName(@RequestParam String name) {
    return memberRepository.findByName(name);
  }

  @GetMapping(value = "", params = "nameLike")
  public Flux<Member> findByNameLike(@RequestParam String nameLike) {
    return memberRepository.findByNameLike("%" + nameLike + "%");
  }

}
