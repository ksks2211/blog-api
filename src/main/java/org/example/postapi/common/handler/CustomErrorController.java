package org.example.postapi.common.handler;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.postapi.common.dto.ApiResponse;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author rival
 * @since 2025-01-14
 */

@Controller
@Slf4j
public class CustomErrorController extends AbstractErrorController {
    private final ErrorProperties errorProperties;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
        this.errorProperties = new ErrorProperties();
    }

    @RequestMapping(value = "${server.error.path:${error.path:/error}}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleError(HttpServletRequest request) {
        Map<String, Object> errorAttributes = getErrorAttributes(request, getErrorAttributeOptions(request));

        HttpStatus status = getStatus(request);

        log.info("Request Error Attributes : {}", errorAttributes);
        if(status.is5xxServerError() && request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) instanceof Exception e) {
            log.error("Internal Server Exception",e);
        }

        var body = ApiResponse.error("Fail to process request", errorAttributes.get("message"));
        return ResponseEntity.status(status).body(body);
    }



    private ErrorAttributeOptions getErrorAttributeOptions(HttpServletRequest request) {
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults();
        options = options.including(ErrorAttributeOptions.Include.MESSAGE);
        if (this.errorProperties.isIncludeException()) {
            options = options.including(ErrorAttributeOptions.Include.EXCEPTION);
        }
        options = isIncludePath(request) ? options.including(ErrorAttributeOptions.Include.PATH) : options.excluding(ErrorAttributeOptions.Include.PATH);
        return options;
    }



    private boolean isIncludePath(HttpServletRequest request) {
        return switch (this.errorProperties.getIncludePath()) {
            case ALWAYS -> true;
            case ON_PARAM -> getPathParameter(request);
            case NEVER -> false;
        };
    }
}
