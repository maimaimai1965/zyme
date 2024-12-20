package ua.mai.zyme.r2dbcmysql.exception;

import org.springframework.http.HttpStatus;

import java.text.MessageFormat;
import java.util.function.Supplier;

public interface FaultInfo extends Supplier<FaultException> {

    HttpStatus DEFAUILT_SERVICE_FAULT_ERROR_HTTPSTATUS =  HttpStatus.INTERNAL_SERVER_ERROR;  // 500

    String UNEXPECTED_ERROR_CODE = "ERR001";
    String UNEXPECTED_ERROR_TEMPLATE =  "Unexpected error: {0}";
    HttpStatus UNEXPECTED_ERROR_HTTPSTATUS =  DEFAUILT_SERVICE_FAULT_ERROR_HTTPSTATUS;

    String NOT_FOUND_CODE = "ERR002";
    String NOT_FOUND_TEMPLATE = "Resource {0} not found";
    HttpStatus NOT_FOUND_HTTPSTATUS =  HttpStatus.NOT_FOUND;

    String code();
    String messageTemplate();
    HttpStatus httpStatus();

    @Override
    default FaultException get() {
        return new FaultException(this);
    }

    static String createParamFor_NOT_FOUND(String resourceName, String resourceIdFieldName, String resourceIdValue) {
        return MessageFormat.format("{0} (for {1}={2})", resourceName, resourceIdFieldName, resourceIdValue);
    }

    static String createMessageFor_UNEXPECTED_ERROR(String errorMessage) {
        return MessageFormat.format(UNEXPECTED_ERROR_TEMPLATE, errorMessage);
    }

}
