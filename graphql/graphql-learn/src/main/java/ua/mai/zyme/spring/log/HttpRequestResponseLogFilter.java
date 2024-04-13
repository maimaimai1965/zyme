package ua.mai.zyme.spring.log;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.*;
import java.util.*;

@Component
@Order(1)
public class HttpRequestResponseLogFilter extends OncePerRequestFilter {

    private final static Logger LOG = LoggerFactory.getLogger(LogServiceImpl.class);


    @Value("${spring.log.request.include-payload}")
    private boolean requestIncludePayload;
    @Value("${spring.log.request.max-payload-length}")
    private int requestMaxPayloadLength;
    @Value("${spring.log.response.include-payload}")
    private boolean responseIncludePayload;
    @Value("${spring.log.response.max-payload-length}")
    private int responseMaxPayloadLength;


    @Autowired
    private LogService loggingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        try {
            // We CANNOT simply read the request payload here, because then the InputStream would be consumed and cannot be read again by the actual processing/server.
            ContentCachingRequestWrapper requestWrapper = request instanceof ContentCachingRequestWrapper req
                    ? req
                    : new ContentCachingRequestWrapper(request);

//            if (!isAsyncDispatch(request) && !(response instanceof ContentCachingResponseWrapper)) {
            ContentCachingResponseWrapper responseWrapper = request instanceof ContentCachingResponseWrapper req
                    ? req
                    : new ContentCachingResponseWrapper(response);

            String requestBody = null;
            try {
                loggingService.logRequestStart(requestWrapper);
                filterChain.doFilter(requestWrapper, responseWrapper);
            } finally {
                // can only log the request's body AFTER the request has been made and ContentCachingRequestWrapper did its work.
                requestBody = requestIncludePayload
                        ? getContentAsString(requestWrapper.getContentAsByteArray(), requestMaxPayloadLength, request.getCharacterEncoding())
                        : null;
//                Map<String, String> requestMap = this.getTypesafeRequestMap(requestWrapper);
//                LOG.debug("[REQ_IN detailed] Method=" + requestWrapper.getMethod() + " URI=" + requestWrapper.getRequestURI()
//                        + " Params=" + requestMap + (requestBody.length() > 0 ? (" Body=" + requestBody) : ""));
                loggingService.logRequestEnd(requestWrapper, requestBody);
            }
            String finalRequestBody = requestBody;
            long duration = System.currentTimeMillis() - startTime;

            if (requestWrapper.isAsyncStarted()) {
                requestWrapper.getAsyncContext().addListener(new AsyncListener() {
                    public void onComplete(AsyncEvent asyncEvent) throws IOException {
                        String responseBody = responseIncludePayload
                                ? getContentAsString(responseWrapper.getContentAsByteArray(), responseMaxPayloadLength, response.getCharacterEncoding())
                                : null;
//                        LOG.debug("[RESP_OUT] Status=" + response.getStatus() + " Duration=" + duration + "ms"
//                                + (includeResponsePayload && str.length() > 0 ? (" Body=" + str) : ""));
                        loggingService.logResponse(requestWrapper, finalRequestBody, responseWrapper, responseBody, duration);
                        responseWrapper.copyBodyToResponse();    // IMPORTANT: copy content of response back into original response
                    }
                    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                    }
                    public void onError(AsyncEvent asyncEvent) throws IOException {
                    }
                    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
                    }
                });
            } else {
                String responseBody = responseIncludePayload
                        ? getContentAsString(responseWrapper.getContentAsByteArray(), responseMaxPayloadLength, response.getCharacterEncoding())
                        : null;
//                String str = getContentAsString(responseWrapper.getContentAsByteArray(), responseMaxPayloadLength, response.getCharacterEncoding());
//                LOG.debug("[RESP_OUT] Status=" + response.getStatus() + " Duration=" + duration + "ms"
//                        + (includeResponsePayload && str.length() > 0 ? (" Body=" + str.length()) : ""));
                loggingService.logResponse(requestWrapper, finalRequestBody, responseWrapper, responseBody, duration);
                responseWrapper.copyBodyToResponse();
            }
        } catch (Throwable e) {
            LOG.error(e.getMessage());
        }
    }

    private String getContentAsString(byte[] buf, int maxLength, String charsetName) {
        if (buf == null || buf.length == 0) return "";
        int length = Math.min(buf.length, maxLength);
        int realLength = length < buf.length ? length - 3 : length;
        try {
            String str = new String(buf, 0, realLength, charsetName);
            return length < buf.length ? str + "..." : str;
        } catch (UnsupportedEncodingException ex) {
            return "Unsupported Encoding";
        }
    }

    private Map<String, String> getTypesafeRequestMap(HttpServletRequest request) {
        Map<String, String> typesafeRequestMap = new HashMap<String, String>();
        Enumeration<?> requestParamNames = request.getParameterNames();
        while (requestParamNames.hasMoreElements()) {
            String requestParamName = (String) requestParamNames.nextElement();
            String requestParamValue;
            if (requestParamName.equalsIgnoreCase("password")) {
                requestParamValue = "********";
            } else {
                requestParamValue = request.getParameter(requestParamName);
            }
            typesafeRequestMap.put(requestParamName, requestParamValue);
        }
        return typesafeRequestMap;
    }

    private String getResponsePayload(HttpServletResponse response) {
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {

            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, 5120);
                try {
                    return new String(buf, 0, length, wrapper.getCharacterEncoding());
                }
                catch (UnsupportedEncodingException ex) {
                    // NOOP
                }
            }
        }
        return "[unknown]";
    }

}