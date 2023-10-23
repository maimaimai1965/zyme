package ua.mai.zyme.security.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Проверяет есть ли в Header-е параметр "Not-Process". Если есть, то возвращает ответ со статусом <i>400 Bad Request</i>.
 */
public class BeforeAuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(BeforeAuthenticationFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;

        String notProcess = httpRequest.getHeader("Not-Process");
        if (notProcess != null) {
            logger.info("BeforeAuthenticationFilter: request {} contains header Not-Process", httpRequest.getRequestURI());
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        logger.info("BeforeAuthenticationFilter: process request = {}", httpRequest.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
