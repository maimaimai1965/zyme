package ua.mai.zyme.r2dbcmysql.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;


@Builder
@Data
public class Member {

  @Id
  @Column("member_id")
  private Integer memberId;

  private String name;
}
