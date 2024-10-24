package ua.mai.zyme.r2dbcmysql.webclient.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.reactive.function.client.ClientResponse;

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

    public AppClientError(ClientResponse clientResponse, String errBody) {
        this.errorBody = errBody;
        if (errorBody.contains("\"errorCd\"")) {
            // Если это ошибка - FaultException.
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                this.clientFaultInfo = objectMapper.readValue(errorBody, ClientFaultInfo.class);
            } catch (JsonProcessingException e) {
                this.clientFaultInfo = new ClientFaultInfo();
                this.clientFaultInfo.setPath(clientResponse.request().getURI().getPath());
                this.clientFaultInfo.setStatus(String.valueOf(clientResponse.statusCode().value()));
                this.clientFaultInfo.setErrorMsg(e.getMessage());
            }
        }  else {
            this.clientFaultInfo = new ClientFaultInfo();
            this.clientFaultInfo.setPath(clientResponse.request().getURI().getPath());
            this.clientFaultInfo.setStatus(String.valueOf(clientResponse.statusCode().value()));
        }
    }

    @Override
    public String getMessage() {
        return "AppClientError: " +
                addStr("Code", clientFaultInfo.getErrorCd()) +
                addStr("Message", clientFaultInfo.getErrorMsg()) +
                addStr("Path", clientFaultInfo.getPath()) +
                addStr("RequestId", clientFaultInfo.getRequestId());
    }

    private static String addStr(String name, Object value) {
        return value != null && value.toString() != null
                ? name + "=" + value + "; "
                : name;
    }

//    public AppClientError(ClientFaultInfo clientFaultInfo, Throwable cause) {
//        super(cause);
//        this.clientFaultInfo = clientFaultInfo;
//    }

}
