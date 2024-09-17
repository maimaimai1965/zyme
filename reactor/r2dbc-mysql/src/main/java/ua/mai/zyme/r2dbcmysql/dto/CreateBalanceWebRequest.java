package ua.mai.zyme.r2dbcmysql.dto;

import lombok.Data;

@Data
public class CreateBalanceWebRequest {

  private Integer memberId;
  private Long balance;
}
