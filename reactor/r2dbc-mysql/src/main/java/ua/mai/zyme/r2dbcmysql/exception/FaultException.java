package ua.mai.zyme.r2dbcmysql.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class FaultException extends RuntimeException {

    private FaultDetails faultDetails;

    public FaultException(FaultInfo faultInfo) {
        this(faultInfo, List.of());
    }

    public FaultException(FaultInfo faultInfo, Object...errorParameters ) {
        this(null, faultInfo, errorParameters);
    }

    public FaultException(Throwable cause, FaultInfo faultInfo, Object...errorParameters) {
        super(cause);
        faultDetails = new FaultDetails(faultInfo, errorParameters);
    }

    public FaultException(Throwable cause) {
        super(cause);
        faultDetails = new FaultDetails(AppFaultInfo.UNEXPECTED_ERROR, cause.getMessage());
    }


    public String getCode() {
        return faultDetails.faultInfo.code();
    }

    public String getMessage() {
        return faultDetails.getMessage();
    }

    public HttpStatus getHttpStatus() {
        return faultDetails.faultInfo.httpStatus();
    }

    public String getCauseMessage() {
        return getCause() != null ? getCause().getMessage() : null;
    }

    public List<Object> getErrorParameters() {
        return faultDetails.errorParameters;
    }

}
