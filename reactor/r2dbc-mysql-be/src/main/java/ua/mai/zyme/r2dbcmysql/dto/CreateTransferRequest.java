package ua.mai.zyme.r2dbcmysql.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateTransferRequest {

  private Integer fromMemberId;
  private Integer toMemberId;
  private Long amount;
}