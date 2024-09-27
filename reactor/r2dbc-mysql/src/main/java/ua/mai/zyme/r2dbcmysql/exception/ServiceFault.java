package ua.mai.zyme.r2dbcmysql.exception;

import java.util.List;

public class ServiceFault extends RuntimeException {

    private FaultDetails faultDetails;

    public ServiceFault(FaultInfo faultInfo) {
        this(faultInfo, List.of());
    }

    public ServiceFault(FaultInfo faultInfo, Object...errorParameters ) {
        this(null, faultInfo, errorParameters);
    }

    public ServiceFault(Throwable cause, FaultInfo faultInfo, Object...errorParameters) {
        super(cause);
        faultDetails = new FaultDetails(faultInfo, errorParameters);
    }

    public ServiceFault(Throwable cause) {
        super(cause);
        faultDetails = new FaultDetails(AppFaultInfo.UNEXPECTED_ERROR, cause.getMessage());
    }


    public String getCode() {
        return faultDetails.faultInfo.code();
    }

    public String getMessage() {
        return faultDetails.getMessage();
    }

    public String getCauseMessage() {
        return getCause() != null ? getCause().getMessage() : null;
    }

    public List<Object> getErrorParameters() {
        return faultDetails.errorParameters;
    }

}
