package ua.mai.zyme.rest.exceptions;

public class RestFaultDataException extends RestFaultException {

    private Object data;

    public RestFaultDataException(Object data, RestFaultInfo restFaultInfo) {
        this(data, restFaultInfo, (String[]) null);
    }

    public RestFaultDataException(Object data, RestFaultInfo restFaultInfo, String...errorMsgParams) {
        super(restFaultInfo, errorMsgParams);
        this.data = data;
    }

    public RestFaultDataException(Object data, RestFaultInfo restFaultInfo, Throwable cause) {
        this(data, restFaultInfo, cause, (String[]) null);
    }

    public RestFaultDataException(Object data, RestFaultInfo restFaultInfo, Throwable cause, String...errorMsgParams) {
        super(cause, restFaultInfo, errorMsgParams);
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    @Override
    public Object toBody() {
        return RestFaultDataBody.builder()
                .errorCd(getErrorCd())
                .errorMsg(getFormattedErrorMsg())
                .data(getData())
                .build();
    }

}
