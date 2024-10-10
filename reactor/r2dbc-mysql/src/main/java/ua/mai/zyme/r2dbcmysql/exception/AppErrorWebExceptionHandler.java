package ua.mai.zyme.r2dbcmysql.exception;

import org.apache.commons.logging.Log;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpLogging;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AppErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    private static final Log logger = HttpLogging.forLogName(AbstractErrorWebExceptionHandler.class);

    // Какие аттрибуты ошибки и в каком порядке логируются.
    private static List<String> attributesInErrorLog =
            List.of("requestId", "errorCd", "errorMsg", "detailMsg", "status", "path", "timestamp");

    private static Map<String, String> attributeQuotationMarks =
            Map.of("errorMsg", "\"",
                    "detailMsg", "\"",
                    "error", "\"");


    public AppErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                       WebProperties.Resources resources,
                                       ErrorProperties errorProperties,
                                       ApplicationContext applicationContext) {
        super(errorAttributes, resources, errorProperties, applicationContext);
    }

    @Override
    protected void logError(ServerRequest request, ServerResponse response, Throwable throwable) {
        Map<String, Object> errorAttributes = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        if (errorAttributes.containsKey("errorCd")) {
            logger.error(formatAppError(throwable, request, errorAttributes));
        } else
            super.logError(request, response, throwable);
    }

    private String formatAppError(Throwable ex, ServerRequest request, Map<String, Object> errorAttributes) {
        String reason = ex.getClass().getSimpleName() + ": " +
                attributesInErrorLog.stream()
                        .filter(attribute -> attributesInErrorLog.contains(attribute) &&
                                             errorAttributes.containsKey(attribute))
                        .map(key -> infoForElement(errorAttributes, key))
                        .collect(Collectors.joining(", ", "{", "}"));
        return "Resolved [" + reason + "] for HTTP " + request.method() + " " + request.path();
    }

    private String infoForElement(Map<String, Object> errorAttributes, String key) {
        return key + "=" + getAttributeQuotation(key) + errorAttributes.get(key) + getAttributeQuotation(key);
    }

    private static String getAttributeQuotation(String key) {
        return attributeQuotationMarks.containsKey(key) ? attributeQuotationMarks.get(key) : "";
    }

}