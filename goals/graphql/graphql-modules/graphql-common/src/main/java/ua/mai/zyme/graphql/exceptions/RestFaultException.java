package ua.mai.zyme.rest.exceptions;

import org.apache.logging.log4j.message.ParameterizedMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 */
public class RestFaultException extends RuntimeException {

    // Throwable cause - находится в родительском классе
    private RestFaultInfo restFaultInfo;
    private String errorCd;
    private String errorMsg;
    private Object[] errorMsgParams;
    private HttpStatus httpStatus;



    public RestFaultException(RestFaultInfo restFaultInfo) {
        this(restFaultInfo, (String[]) null);
    }

    public RestFaultException(RestFaultInfo restFaultInfo, String...errorMsgParams) {
        super();
        this.errorMsgParams = errorMsgParams;
    }

    public RestFaultException(Throwable cause, RestFaultInfo restFaultInfo) {
        this(cause, restFaultInfo, (String[]) null);
    }

    public RestFaultException(Throwable cause, RestFaultInfo restFaultInfo, String...errorMsgParams) {
        super(cause);
        this.errorMsgParams = errorMsgParams;
    }

    protected RestFaultException withRestFaultInfo(RestFaultInfo restFaultInfo) {
        this.restFaultInfo = restFaultInfo;
        if (restFaultInfo != null) {
            this.errorCd = restFaultInfo.code();
            this.errorMsg = restFaultInfo.message();
            this.httpStatus = restFaultInfo.httpStatus();
        }
        return this;
    }

    public RestFaultException withErrorCd(String errorCd) {
        this.errorCd = errorCd;
        return this;
    }

    public RestFaultException withErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public RestFaultException withErrorMsgParams(Object...errorMsgParams) {
        this.errorMsgParams = errorMsgParams;
        return this;
    }

    public RestFaultException withHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public RestFaultInfo getRestFaultInfo() {
        return restFaultInfo;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return getFormattedErrorMsg();
    }

    public String getFormattedErrorMsg() {
        return format(errorMsg, errorMsgParams);
    }

    public ResponseEntity toResponseEntity() {
        return new ResponseEntity<>(toBody(), httpStatus);
    }

    public Object toBody() {
        return RestFaultDataBody.builder()
                .errorCd(getErrorCd())
                .errorMsg(getFormattedErrorMsg())
                .build();
    }

//    public static Object toBody(RestFaultException ex) {
//        Object result;
//        if (ex.detail != null) {
//            result = RestFaultBodyData.builder()
//                        .errorCd(ex.getRestFaultInfo().code())
//                        .errorMsg(ex.getMessage())
//                        .detailMsg(ex.detail.toString())
//                        .build();
//        } else {
//            result = RestFaultBody.builder()
//                        .errorCd(ex.getRestFaultInfo().code())
//                        .errorMsg(ex.getMessage())
//                        .build();
//        }
//        return result;
//    }

    public String getErrorCd() {
        return errorCd;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Object[] getErrorMsgParams() {
        return errorMsgParams;
    }

    public static String format(final String messagePattern, Object... arguments) {
        return ParameterizedMessage.format(messagePattern, arguments);
    }

}
