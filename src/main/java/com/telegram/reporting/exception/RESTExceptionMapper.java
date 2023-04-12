
package com.telegram.reporting.exception;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class RESTExceptionMapper {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMethodArgumentException(MissingServletRequestParameterException e, ServletWebRequest servletRequest) {
        return createResponse(e, servletRequest, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e, ServletWebRequest servletRequest) {
        val httpRequest = servletRequest.getRequest();
        val httpStatus = HttpStatus.BAD_REQUEST;

        val apiExceptionTOS = e.getConstraintViolations().stream()
                .map(cv -> new ApiExceptionTO(
                        cv.getMessage(),
                        httpRequest.getMethod(),
                        httpRequest.getServletPath(),
                        httpStatus))
                .toList();

        return createResponse(apiExceptionTOS, httpStatus);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNotFoundException(NoSuchElementException e, ServletWebRequest servletRequest) {
        return createResponse(e, servletRequest, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException e, ServletWebRequest servletRequest) {
        return createResponse(e, servletRequest, HttpStatus.CONFLICT);
    }

    private ResponseEntity<Object> createResponse(List<ApiExceptionTO> apiExceptionTOS, HttpStatus httpStatus) {
        logError(apiExceptionTOS);
        return new ResponseEntity<>(apiExceptionTOS, httpStatus);
    }

    private ResponseEntity<Object> createResponse(Exception e, ServletWebRequest servletRequest, HttpStatus statusCode) {
        val httpRequest = servletRequest.getRequest();
        val message = e.getMessage();
        val apiException = new ApiExceptionTO(message, httpRequest.getMethod(), httpRequest.getServletPath(), statusCode);

        logError(apiException);
        return new ResponseEntity<>(apiException, statusCode);
    }

    private <T> void logError(T apiException) {
        log.error("Error in REST API. Error details - {}", apiException);
    }

    /**
     * This class is used to transfer error details to the client.
     * detail - human-readable error message
     * method - HTTP method
     * path - HTTP path
     * httpStatus - HTTP status code
     */
    public record ApiExceptionTO(String detail, String method, String path, HttpStatus httpStatus) {
    }
}
