package ua.mai.zyme.rest.zoo.exceptions;

import org.apache.logging.log4j.message.ParameterizedMessage;
import org.springframework.http.HttpStatus;
import ua.mai.zyme.rest.zoo.models.ClientIdException;
import ua.mai.zyme.rest.zoo.models.DataDocException;
import ua.mai.zyme.rest.zoo.models.DetailException;
import ua.mai.zyme.rest.zoo.models.Doc;

public class RestFaultException extends RuntimeException {

    private RestFaults restFault;
    private HttpStatus httpStatus;

    private Object data;

    private String clientId;

    public RestFaultException(RestFaults restFault, Throwable cause) {
        this(restFault, restFault.message(), restFault.httpStatus(), null, cause);
    }

    public RestFaultException(RestFaults restFault, Object data, Throwable cause) {
        this(restFault, restFault.message(), restFault.httpStatus(), data, cause);
    }

    public RestFaultException(RestFaults restFault, HttpStatus httpStatus, Throwable cause) {
        this(restFault, restFault.message(), httpStatus, null, cause);
    }

    public RestFaultException(RestFaults restFault, String message, HttpStatus httpStatus, Object data, Throwable cause) {
        super(message, cause);
        this.restFault = restFault;
        this.httpStatus = httpStatus;
        this.data = data;
    }

    public RestFaultException(RestFaults restFault, Throwable cause, String...params) {
        this(restFault, restFault.httpStatus(), null, cause, params);
    }

    public RestFaultException(RestFaults restFault, Object data, Throwable cause, String...params) {
        this(restFault, restFault.httpStatus(), data, cause, params);
    }

    public RestFaultException(RestFaults restFault, HttpStatus httpStatus, Throwable cause, String...params) {
        this(restFault, httpStatus, null, cause, params);
    }

    public RestFaultException(RestFaults restFault, HttpStatus httpStatus, Object data, Throwable cause, String...params) {
        this(restFault, restFault.message(), httpStatus, data, cause, params);
    }

    public RestFaultException(RestFaults restFault, String message, HttpStatus httpStatus, Object data, Throwable cause, String...params) {
        super(format(message, params), cause);
        this.restFault = restFault;
        this.httpStatus = httpStatus;
        this.data = data;
    }

    public RestFaults getRestFault() {
        return restFault;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Object getData() {
        return data;
    }

    public static Object toBody(RestFaultException ex) {
        Object result = null;
        if (ex.data != null) {
            if (ex.data instanceof Doc) {
                result = DataDocException.builder()
                      .errorCd(ex.getRestFault().code())
                      .errorMsg(ex.getMessage())
                      .data(new Doc[]{(Doc) ex.data})
                      .build();
//            } else if (ex.data instanceof QuotaRefillTransaction) {
//                result = DataTransactionException.builder()
//                      .errorCd(ex.getRestFault().code())
//                      .errorMsg(ex.getMessage())
//                      .data(new QuotaRefillTransaction[]{(QuotaRefillTransaction) ex.data})
//                      .build();
            }  else {
                result = DetailException.builder()
                      .errorCd(ex.getRestFault().code())
                      .errorMsg(ex.getMessage())
                      .detailMsg(ex.data.toString())
                      .build();
            }
        } else if (ex.clientId != null) {
            result = ClientIdException.builder()
                  .errorCd(ex.getRestFault().code())
                  .errorMsg(ex.getMessage())
                  .clientId(ex.clientId)
                  .build();
//        } else {
//            result = SimpleException.builder()
//                  .errorCd(ex.getRestFault().code())
//                  .errorMsg(ex.getMessage())
//                  .build();
        }
        return result;
    }

    public static String format(final String messagePattern, Object... arguments) {
        return ParameterizedMessage.format(messagePattern, arguments);
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

//    public String formatMessage(Object... arguments) {
//        return ParameterizedMessage.format(restFault.message(), arguments);
//    }

}
