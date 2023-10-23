package ua.mai.zyme.security.filters;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger("Authentication");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;

        if (httpRequest.getRequestURI().startsWith("/hiWithStaticKey")) {
            // Если попали сюда - это значит, что авторизация для URL произошла по authorization static key (в StaticKeyAuthenticationFilter).
            logger.info("AuthenticationLoggingFilter: Authorization by static key");
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            logger.info("AuthenticationLoggingFilter: Successfully authenticated request for user {}", authentication.getName());
//            if (authentication == null) {
//                logger.info("AuthenticationLoggingFilter: bad user or password");
//                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                return;
//            } else {
//                logger.info("AuthenticationLoggingFilter: Successfully authenticated request for user {}",
//                        ((User) authentication.getPrincipal()).getUsername());
//            }
        }

        filterChain.doFilter(request, response);
    }

}
