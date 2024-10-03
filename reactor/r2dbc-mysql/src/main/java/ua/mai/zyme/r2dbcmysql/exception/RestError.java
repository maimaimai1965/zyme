package ua.mai.zyme.r2dbcmysql.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Builder
public class RestError {

  private String errorCd;
  private String errorMsg;
  private String detailMsg;

}
