package ua.mai.zyme.rest.zoo.exceptions;

public class ServiceException extends RuntimeException {

    private int errorCode;
    private String message;

    private String partnerAccCd;

    public ServiceException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public ServiceException(int errorCode, String message) {
        this(errorCode, message, null);
    }

    public ServiceException(int errorCode, String message, String partnerAccCd) {
        super();
        this.errorCode = errorCode;
        this.message = message;
        this.partnerAccCd = partnerAccCd;
    }


    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getPartnerAccCd() {
        return partnerAccCd;
    }

}
