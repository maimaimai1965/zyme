package ua.mai.zyme.rest.zoo.controllers;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mai.zyme.rest.exceptions.RestFaultDetailMsgException;
import ua.mai.zyme.rest.exceptions.RestFaults;

import javax.servlet.http.HttpServletRequest;

@RestController()
@RequestMapping("${zyme.rest-services.zoo.api-url}")
public class ZooDefaultApiController {

    private static final Logger LOG = LoggerFactory.getLogger("rest.zoo.controller");

    @RequestMapping(value = "/*", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> checkApi(HttpServletRequest request) {
        LOG.debug("ZooDefaultApiController: Unsupported URL {}", request.getRequestURI());
        throw new RestFaultDetailMsgException(request.getMethod() + " url: " + request.getRequestURI(),
                        RestFaults.UNSUPPORTED_URL);
    }

    @RequestMapping(value = "/*")
    public ResponseEntity<Object> checkMediaType(HttpServletRequest request) {
        String contentType = request.getContentType();
        LOG.debug("ZooDefaultApiController: Unsupported media type {}", contentType);
        throw new RestFaultDetailMsgException("Content type '" + contentType + "' not supported",
                RestFaults.UNSUPPORTED_MEDIA_TYPE);
    }

}