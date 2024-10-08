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
        return getFaultMessage();
    }

    public HttpStatus getHttpStatus() {
        return faultDetails.faultInfo.httpStatus();
    }

    public String getFaultMessage() {
        return faultDetails.getMessage();
    }

    public String getCauseMessage() {
        return getCause() != null ? getCause().getMessage() : null;
    }

    public List<Object> getErrorParameters() {
        return faultDetails.errorParameters;
    }

    public String toString() {
        return "Code=" + getCode() +
               ", Message=\"" + getFaultMessage() + "\"" +
                (getCause() != null ? (", Details=" + getCause()) : "") +
               ", HttpStatus=" + getHttpStatus();
    }

}
