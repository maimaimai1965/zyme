package ua.mai.zyme.rest.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RestFaultDataBody {
    private String errorCd;
    private String errorMsg;
    private Object data;
}
