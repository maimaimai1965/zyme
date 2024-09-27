package ua.mai.zyme.r2dbcmysql.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class CreateBalanceRequest {

  private Integer memberId;
  private Long amount;
  private LocalDateTime createdDate;
}
