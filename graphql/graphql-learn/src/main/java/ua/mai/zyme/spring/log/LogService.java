package ua.mai.zyme.spring.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LogService {
    void logRequestStart(HttpServletRequest request);
    void logRequestEnd(HttpServletRequest request, String requestBody);
    void logResponse(HttpServletRequest request, String requestBody, HttpServletResponse response, String responseBody, long duration);
}
