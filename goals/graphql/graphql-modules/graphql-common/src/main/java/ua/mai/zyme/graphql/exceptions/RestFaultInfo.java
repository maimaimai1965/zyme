package ua.mai.zyme.rest.exceptions;

import org.springframework.http.HttpStatus;

public interface RestFaultInfo {

    String code();
    String message();
    HttpStatus httpStatus();

}
