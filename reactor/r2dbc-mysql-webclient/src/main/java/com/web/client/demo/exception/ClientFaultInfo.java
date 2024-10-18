package com.web.client.demo.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class ClientFaultInfo {

//    private LocalDateTime timestamp;
    private String path;
    private String error;
    private String requestId;
    private String status;
    private String errorCd;
    private String errorMsg;

}
