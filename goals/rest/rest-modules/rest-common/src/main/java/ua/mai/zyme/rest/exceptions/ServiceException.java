package ua.mai.zyme.rest.exceptions;

public class ServiceException extends RuntimeException {

    private int errorCode;
    private String message;

    public ServiceException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public ServiceException(int errorCode, String message) {
        super();
        this.errorCode = errorCode;
        this.message = message;
    }


    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
