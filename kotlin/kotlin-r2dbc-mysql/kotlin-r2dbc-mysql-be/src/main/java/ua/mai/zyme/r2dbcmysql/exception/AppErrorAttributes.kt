package ua.mai.zyme.r2dbcmysql.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
public class AppErrorAttributes<T extends Throwable> extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
        addErrorDetails(errorAttributes, request);
        return errorAttributes;
    }

    protected void addErrorDetails(Map<String, Object> errorAttributes, ServerRequest request) {
        errorAttributes.remove("trace");
        Throwable ex = getError(request);

        if (ex != null) {
            errorAttributes.remove("message");

            if (ex instanceof FaultException faultException) {
                errorAttributes.remove("status");

                errorAttributes.put("status", faultException.getHttpStatus().value());
                errorAttributes.put("errorCd", faultException.getCode());
                errorAttributes.put("errorMsg", faultException.getMessage());
                if (faultException.getCauseMessage() != null) {
                    errorAttributes.put("detailMsg", faultException.getCauseMessage());
                }
            } else {
                errorAttributes.put("errorCd", FaultInfo.UNEXPECTED_ERROR_CODE);
                errorAttributes.put("errorMsg", FaultInfo.createMessageFor_UNEXPECTED_ERROR(ex.getMessage()));
                if (ex.getCause() != null) {
                    errorAttributes.put("detailMsg", ex.getCause().getMessage());
                }
            }
        }
    }

}