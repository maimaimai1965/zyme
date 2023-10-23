package ua.mai.zyme.rest.exceptions;

public class RestFaultDetailMsgException extends RestFaultException {

    private String detailMessage;

    public RestFaultDetailMsgException(String detailMessage, RestFaultInfo restFaultInfo) {
        this(detailMessage, restFaultInfo, (String[]) null);
    }

    public RestFaultDetailMsgException(String detailMessage, RestFaultInfo restFaultInfo, String...errorMsgParams) {
        super(restFaultInfo, errorMsgParams);
        this.detailMessage = detailMessage;
    }

    public RestFaultDetailMsgException(Throwable cause, String detailMessage, RestFaultInfo restFaultInfo) {
        this(cause, detailMessage, restFaultInfo, (String[]) null);
    }

    public RestFaultDetailMsgException(Throwable cause, String detailMessage, RestFaultInfo restFaultInfo, String...errorMsgParams) {
        super(cause, restFaultInfo, errorMsgParams);
        this.detailMessage = detailMessage;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    @Override
    public Object toBody() {
        return RestFaultDetailMsgBody.builder()
                .errorCd(getErrorCd())
                .errorMsg(getFormattedErrorMsg())
                .detailMsg(getDetailMessage())
                .build();
    }

}
