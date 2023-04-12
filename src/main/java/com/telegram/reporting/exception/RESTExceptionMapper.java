
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
    public ResponseEntity<Object> handleMethodArgumentException(MissingServletRequestParameterException ex, ServletWebRequest servletRequest) {
        val httpRequest = servletRequest.getRequest();
        return createResponse(
                ex.getMessage(),
                httpRequest.getMethod(),
                httpRequest.getServletPath(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, ServletWebRequest servletRequest) {
        val httpRequest = servletRequest.getRequest();
        val httpStatus = HttpStatus.BAD_REQUEST;

        val apiExceptionTOS = ex.getConstraintViolations().stream()
                .map(cv -> new ApiExceptionTO(
                        cv.getMessage(),
                        httpRequest.getMethod(),
                        httpRequest.getServletPath(),
                        httpStatus))
                .toList();

        return createResponse(apiExceptionTOS, httpStatus);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNotFoundException(NoSuchElementException ex, ServletWebRequest servletRequest) {
        val httpRequest = servletRequest.getRequest();
        return createResponse(
                ex.getMessage(),
                httpRequest.getMethod(),
                httpRequest.getServletPath(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException ex, ServletWebRequest servletRequest) {
        val httpRequest = servletRequest.getRequest();
        return createResponse(
                ex.getMessage(),
                httpRequest.getMethod(),
                httpRequest.getServletPath(),
                HttpStatus.CONFLICT);
    }

    private ResponseEntity<Object> createResponse(List<ApiExceptionTO> apiExceptionTOS, HttpStatus httpStatus) {
        log.error("Error in REST API. Errors - {}", apiExceptionTOS);
        return new ResponseEntity<>(apiExceptionTOS, httpStatus);
    }

    private ResponseEntity<Object> createResponse(String message, String method, String path, HttpStatus statusCode) {
        log.error("Error in REST API. Errors - {}. StatusCode - {}", message, statusCode);
        return new ResponseEntity<>(
                new ApiExceptionTO(message, method, path, statusCode),
                statusCode);
    }

    public record ApiExceptionTO(String detail, String method, String path, HttpStatus httpStatus) {
    }
}
