package ua.mai.zyme.r2dbcmysql.webclient.log;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.http.HttpField;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LogService {
    /**
     * @param inboundRequest of type jetty client Request Object
     * @return return same request again after preparing logging
     */
    public Request logRequestResponse(Request inboundRequest, String targetServiceName) {
        // Created a string builder objects to append logs belongs to specific request
        StringBuilder logRequestBuilder = new StringBuilder();
        StringBuilder logResponseBuilder = new StringBuilder();
        boolean[] first = new boolean[]{true};


        // Request Logging ---------------------------------------------------------------------------------------------

        /* This listener will be invoked when the request is being processed in order to be sent.
         */
        inboundRequest.onRequestBegin(request -> logRequestBuilder
                        .append("WEBCLIENT REQ (to ")
                        .append(targetServiceName)
                        .append(")\n  Address: ").append(request.getURI())
                        .append("\n  HttpMethod: ").append(request.getMethod())
        );

        /*  This listener will be invoked when the request headers are ready to be sent.
            When this is invoked we are appending Headers to string builder with proper format.
         */
        inboundRequest.onRequestHeaders(request -> {
            if (log.isDebugEnabled()) {
                if (request.getHeaders().stream().count() > 0) {
                    logRequestBuilder.append("\n  Headers: ");
                    for (HttpField header : request.getHeaders()) {
                        logRequestBuilder.append(header.getName()).append(":").append(header.getValue()).append("; ");
                    }
                }
            }
        });

        /*  This listener will be invoked when request content has been sent successfully.
            When this is invoked we are converting bytes to String and appending to StringBuilder .
         */
        inboundRequest.onRequestContent((request, content) ->  {
            if (log.isDebugEnabled()) {
                var bufferAsString = StandardCharsets.UTF_8.decode(content).toString();
                logRequestBuilder.append("\n  Body REQ:\n").append(bufferAsString);
            }
        });

//        inboundRequest.onRequestSuccess(request -> {
//            logInfoDebugMsg(logRequestBuilder.toString());
//        });
//
//        inboundRequest.onRequestFailure((request, throwable) -> {
//            logErrorMsg(logRequestBuilder.toString(), throwable, request);
//        });


        // Response Logging --------------------------------------------------------------------------------------------

        /* This listener will be invoked  when the response containing HTTP version, HTTP status code and reason has been received and parsed.
         */
        inboundRequest.onResponseBegin(response -> { logResponseBuilder
                .append("WEBCLIENT RES (from ")
                .append(targetServiceName)
                .append(")\n  Address: ").append(response.getRequest().getURI())
                .append("\n  HttpMethod: ").append(response.getRequest().getMethod())
                .append("\n  Status: ").append(response.getStatus());
        });

         /* This listener will be invoked when the response headers are received and parsed.
          */
        inboundRequest.onResponseHeaders(response -> {
            if (log.isDebugEnabled()) {
                if (response.getHeaders().stream().count() > 0) {
                    logResponseBuilder.append("\n  Headers: ");
                    for (HttpField header : response.getHeaders()) {
                        logResponseBuilder.append(header.getName()).append(":").append(header.getValue()).append("; ");
                    }
                }
            }
        });

        /* This listener will be invoked when response content has been received and parsed.
           When this is invoked we are converting bytes to String and appending to StringBuilder .
         */
        inboundRequest.onResponseContent(((response, content) -> {
            if (log.isDebugEnabled()) {
                var bufferAsString = StandardCharsets.UTF_8.decode(content).toString();
                if (first[0]) {
                    logResponseBuilder.append("\n  Body RES:");
                    first[0] = false;
                }
                logResponseBuilder.append("\n").append(bufferAsString);
            }
        }));


        inboundRequest.onResponseSuccess(response -> {
            logInfoDebugMsg(logRequestBuilder.toString());
            String logResponse = logResponseBuilder.toString();
            if (logResponse.contains("errorCd"))
                log.error(logResponse);
            else
                logInfoDebugMsg(logResponseBuilder.toString());
        });

        inboundRequest.onResponseFailure((response, throwable) -> {
            Request request = response.getRequest();
            if (logRequestBuilder.isEmpty()) {
                logRequestBuilder
                        .append("WEBCLIENT REQ (to ")
                        .append(targetServiceName)
                        .append(")\n  Address: ").append(request.getURI())
                        .append("\n  HttpMethod: ").append(request.getMethod());
                if (log.isDebugEnabled()) {
                    if (request.getHeaders().stream().count() > 0) {
                        logRequestBuilder.append("\n  Headers: ");
                        for (HttpField header : request.getHeaders()) {
                            logRequestBuilder.append(header.getName()).append(":").append(header.getValue()).append("; ");
                        }
                    }
                }
            }
            logInfoDebugMsg(logRequestBuilder.toString());
            logErrorMsg(logResponseBuilder.toString(), throwable, response.getRequest());
        });

        return inboundRequest;
    }

    private void logInfoDebugMsg(String msg) {
        if (log.isDebugEnabled()) {
            log.debug(msg);
        } else if (log.isInfoEnabled()) {
            log.info(msg);
        }
    }

    private void logErrorMsg(String msg, Throwable throwable, Request request) {
        log.error(msg != null && !msg.isEmpty() ? msg
                   : throwable.getMessage() + "; URI=" + request.getURI()
                , throwable);
    }

}