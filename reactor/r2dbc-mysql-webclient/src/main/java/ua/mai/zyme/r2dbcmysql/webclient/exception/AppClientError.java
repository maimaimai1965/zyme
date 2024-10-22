package ua.mai.zyme.r2dbcmysql.webclient.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class AppClientError extends RuntimeException {

    private ClientFaultInfo clientFaultInfo;
    private String errorBody;

    public ClientFaultInfo getClientFaultInfo() {
        return clientFaultInfo;
    }

    public String getErrorBody() {
        return errorBody;
    }

    //    public AppClientError(ClientFaultInfo clientFaultInfo) {
//        this(clientFaultInfo, null);
//    }
//
//    public AppClientError(Throwable cause) {
//        super(cause);
//    }

    public AppClientError(String errBody) {
        this.errorBody = errBody;
        if (errorBody.contains("\"error\"")) {
            // Если это ошибка - FaultException.
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                this.clientFaultInfo = objectMapper.readValue(errorBody, ClientFaultInfo.class);
            } catch (JsonProcessingException e) {
                //
            }
        }
    }

//    public AppClientError(ClientFaultInfo clientFaultInfo, Throwable cause) {
//        super(cause);
//        this.clientFaultInfo = clientFaultInfo;
//    }

}
