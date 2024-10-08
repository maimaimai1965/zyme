package ua.mai.zyme.r2dbcmysql.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Builder
@Data
public class Transfer {

  @Id
  @Column("transfer_id")
  private Long transferId;

  @Column("from_member_id")
  private Integer fromMemberId;

  @Column("to_member_id")
  private Integer toMemberId;

  private Long amount;

  @Column("created_date")
  private LocalDateTime createdDate;

}

