
package com.telegram.reporting.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class RESTExceptionMapper extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (k1, k2) -> k1));
        return createResponse(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, NativeWebRequest request) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(constraint -> constraint.getPropertyPath().toString(), ConstraintViolation::getMessage, (k1, k2) -> k1));
        return createResponse(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNotFoundException(NoSuchElementException ex, NativeWebRequest request) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        String method = "%s - %s".formatted(servletRequest.getMethod(), servletRequest.getServletPath());
        return createResponse(Map.of(method, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException ex, NativeWebRequest request) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        String method = "%s - %s".formatted(servletRequest.getMethod(), servletRequest.getServletPath());
        String errorMessage = Optional.ofNullable(ex.getMessage())
                .orElse("Unknown error occurred related to duplicate key");
        return createResponse(Map.of(method, errorMessage), HttpStatus.CONFLICT);
    }

    private ResponseEntity<Object> createResponse(Map<String, String> errors, HttpStatus statusCode) {
        Map<String, Map<String, String>> body = new HashMap<>();
        log.error("Error in REST API. Errors - {}. StatusCode - {}", errors, statusCode);
        body.put("errors", errors);
        return new ResponseEntity<>(body, statusCode);
    }
}
