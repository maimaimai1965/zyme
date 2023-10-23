package ua.mai.zyme.rest.zoo.exceptions;

import org.springframework.http.HttpStatus;

public interface RestFaultInfo {

    String code();
    String message();
    HttpStatus httpStatus();

}
