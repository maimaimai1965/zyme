package ua.mai.zyme.r2dbcmysql.repository;

import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import ua.mai.zyme.r2dbcmysql.entity.Member;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface MemberRepository extends R2dbcRepository<Member, Integer> {

  Mono<Member> findByName(String name);

  Flux<Member> findByNameLike(String str);

  @Query("SELECT * FROM member WHERE CHAR_LENGTH(name) <= :length")
  Flux<Member> findByNameLengthLE(int length);

  @Query("INSERT INTO member (name) VALUES (:name)")
  Mono<Void> insertThroughSql(String name);

  @Query("UPDATE member SET name = :name WHERE member_id = :member_id")
  Mono<Void> updateThroughSql(int member_id, String name);

  @Query("DELETE FROM member WHERE member_id = :member_id")
  Mono<Void> deleteThroughSql(int member_id);

}
