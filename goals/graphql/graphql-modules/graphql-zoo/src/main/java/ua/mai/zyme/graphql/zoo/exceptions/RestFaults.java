package ua.mai.zyme.rest.zoo.exceptions;

import org.springframework.http.HttpStatus;

public enum RestFaults implements RestFaultInfo {

    SUCCESS("0", "Success", HttpStatus.OK),
    SERVICE_UNAVAILABLE("1", "The service is temporarily unavailable", HttpStatus.INTERNAL_SERVER_ERROR),
    SYSTEM_ERROR("2", "System error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_ERROR("3", "Unknown error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_LOGIN("4", "Invalid login", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("5", "Access denied", HttpStatus.FORBIDDEN),
    NO_PERMISION("6", "No Permission", HttpStatus.FORBIDDEN),
    NOT_ALLOWED_IP("7", "Request from client with IP is not allowed: {}", HttpStatus.FORBIDDEN),
    INVALID_REQUEST("10", "Invalid request", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_MEDIA_TYPE("10", "Invalid request", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    UNSUPPORTED_URL("10", "Invalid request", HttpStatus.NOT_FOUND),
    MISSED_MANDATORY_PARAMETER("11", "The mandatory parameter is missed: {}", HttpStatus.BAD_REQUEST), // {PARAM}
    INVALID_PARAMETER_VALUE("12", "Invalid parameter value: {}", HttpStatus.BAD_REQUEST), // {PARAM}
    INVALID_PARAMETER_LENGTH("13", "Invalid parameter length: {}", HttpStatus.BAD_REQUEST),  // {PARAM}
    UNSUPPORTED_PARAMETER_VALUE("14", "Unsupported value for parameter '{}': {}", HttpStatus.BAD_REQUEST), // {PARAM} {VALUE}
    MUTUALLY_EXCLUSIVE_PARAMETERS("15", "Mutually exclusive parameters '{}' and '{}'", HttpStatus.BAD_REQUEST), // {PARAM1} {PARAM2}

    ALREADY_EXISTS("100", "Already exists", HttpStatus.CONFLICT),
    UNKNOWN_PARTNER_CODE("101", "Unknown partner code: {}", HttpStatus.NOT_FOUND), // {partnerCode}
    QUOTA_FAILURE("102", "Update partner quota failure", HttpStatus.CONFLICT),
    TRANSACTION_NOT_FOUND("103", "Transaction not found", HttpStatus.NOT_FOUND),

    PARTNER_ACCOUNT_NOT_FOUND("200", "Partner account is not found: {}", HttpStatus.NOT_FOUND),
    PARTNER_ACCOUNT_NOT_QUOTABLE("201", "Partner account does not have quota limit: {}", HttpStatus.CONFLICT),
    MFS_IS_NOT_AVAILABLE("301", "MFS is not available for Partner", HttpStatus.FORBIDDEN),
    NO_SERVICES_FOR_PARTNER("302", "There are no services for Partner", HttpStatus.NOT_FOUND),
    SERVICE_CODE_IS_NOT_FOUND("303", "Service code is not found", HttpStatus.NOT_FOUND),
    SERVICE_CODE_BELONGS_TO_OTHER_PARTNER("303", "Service code belongs to other Partner", HttpStatus.NOT_FOUND),
    SERVICE_CODE_ALREADY_EXISTS("304", "Service code already exists", HttpStatus.CONFLICT),
    //INVALID_LIMIT_TYPE_VALUE("202", "Invalid credit limit type: {}", HttpStatus.CONFLICT),
    //INVALID_CREDIT_LIMIT_NULL("203", "Credit limit can not be NULL", HttpStatus.CONFLICT),
    //MISSED_CREDIT_LIMIT_TMP_REFILL("204", "Temp credit limit should be great zero", HttpStatus.CONFLICT), // {PARAM}
    //MISSED_CREDIT_LIMIT_TILL_DATE("205", "Temp credit limit till date can not be NULL", HttpStatus.CONFLICT), // {PARAM}
    //PA_TERMINATED("206", "Partner not found or terminated: {}", HttpStatus.CONFLICT),
    //PARTNER_TERMINATED_ERR("207", "Partner terminated: {}", HttpStatus.CONFLICT),

    OTHERS_ERROR("110", "Others error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String msg;
    private HttpStatus httpStatus;

    RestFaults(String code, String msg, HttpStatus httpStatus) {
        this.code = code;
        this.msg = msg;
        this.httpStatus = httpStatus;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return msg;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
