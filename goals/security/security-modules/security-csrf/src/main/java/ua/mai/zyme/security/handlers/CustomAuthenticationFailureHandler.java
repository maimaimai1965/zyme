package ua.mai.zyme.security.handlers;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e)  {
//        httpServletResponse.setHeader("failed", LocalDateTime.now().toString());
        try {
            httpServletResponse.sendRedirect("/no_auth");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
