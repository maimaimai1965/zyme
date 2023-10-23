package ua.mai.zyme.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class AuthenticationAuthorizationHandler implements AuthenticationEntryPoint, AccessDeniedHandler  {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        int i = 1;
//        response.setStatus(RestFaults.NO_PERMISION.httpStatus().value());
//        writeFault(RestFaults.NO_PERMISION, response);
    }

    private static final List<String> AccessDeniedFaultCodes = List.of(
//          AuthFaults.ACC_DISABLED_INTERN.code(),
//          AuthFaults.ACC_LOCKED_TMP.code(),
//          AuthFaults.PASS_USED_LAST_TIME.code(),
//          AuthFaults.PASS_REUSE_EXCEEDED.code(),
//          AuthFaults.ACC_EXPIRED.code(),
//          AuthFaults.ACC_LOCKED.code(),
//          AuthFaults.ACC_DISABLED_EXTERN.code()
    );

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        int i = 1;
//        if (authException.getCause() != null && authException.getCause() instanceof ServiceFault) {
//            ServiceFault ex = (ServiceFault) authException.getCause();
//            RestFaults fault = AccessDeniedFaultCodes.contains(ex.getCode()) ? RestFaults.ACCESS_DENIED : RestFaults.INVALID_LOGIN;
//            response.setStatus(fault.httpStatus().value());
//            writeError(fault, ex.getCode() + " " + ex.getMessage(), response);
//        } else {
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            writeError(RestFaults.INVALID_LOGIN, authException.getMessage(), response);
//        }
    }

//    private void writeFault(RestFaults fault, HttpServletResponse response) throws IOException {
//        response.getWriter().write(objectMapper.writeValueAsString(Map.of("errorCd", fault.code(), "errorMsg", fault.message())));
//    }

//    private void writeError(RestFaults restFault, String detailMsg, HttpServletResponse response) throws IOException {
//        response.getWriter().write(objectMapper.writeValueAsString(
//              Map.of("errorCd", restFault.code(), "errorMsg", restFault.message(), "detailMsg", detailMsg)));
//    }

}
