package ua.mai.zyme.spring.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogServiceImpl implements LogService {

    private final static Logger LOG = LoggerFactory.getLogger(LogServiceImpl.class);

    @Override
    public void logRequestStart(HttpServletRequest request) {
        LOG.debug("[REQ_IN start] Method=" + request.getMethod() + " URI=" + request.getRequestURI());
    }

    @Override
    public void logRequestEnd(HttpServletRequest request, String requestBody) {
        LOG.debug("[REQ_IN end] Method=" + request.getMethod() + " URI=" + request.getRequestURI()
//                + " Params=" + requestMap
                + (requestBody.length() > 0 ? (" Body=" + requestBody) : ""));
    }

    @Override
    public void logResponse(HttpServletRequest request, String requestBody, HttpServletResponse response, String responseBody, long duration) {
        LOG.debug("[RESP_OUT] Status=" + response.getStatus() + " Duration=" + duration + "ms"
                + (responseBody != null  && responseBody.length() > 0 ? (" Body=" + responseBody) : ""));
    }

}
