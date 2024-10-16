package ua.mai.zyme.r2dbcmysql.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncreaseBalanceRequest {
  private Integer memberId;
  private Long amount;
  private LocalDateTime modifiedDate;
}
