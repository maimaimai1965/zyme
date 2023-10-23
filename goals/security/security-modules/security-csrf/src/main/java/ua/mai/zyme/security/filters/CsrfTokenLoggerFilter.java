package ua.mai.zyme.security.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;

import javax.servlet.*;
import java.io.IOException;

public class CsrfTokenLoggerFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(CsrfTokenLoggerFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        LOG.info("CSRF token {}", token.getToken());

        filterChain.doFilter(request, response);
    }
}
