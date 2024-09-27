package ua.mai.zyme.r2dbcmysql.exception;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

public class FaultDetails {
    FaultInfo faultInfo;
    List<Object> errorParameters;

    public FaultDetails(FaultInfo faultInfo, Object...errorParameters) {
        this.faultInfo = faultInfo;
        this.errorParameters = (errorParameters != null) ? Arrays.stream(errorParameters).toList() : List.of();
    }

    public String getMessage() {
        return MessageFormat.format(faultInfo.messageTemplate(), errorParameters);
    }

}
