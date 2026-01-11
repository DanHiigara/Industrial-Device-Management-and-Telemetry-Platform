package com.ndiii.common.web;

import com.ndiii.common.api.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, IllegalArgumentException.class})
  public ResponseEntity<ApiError> badRequest(Exception ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError(Instant.now(), req.getRequestURI(), "Bad Request", ex.getMessage(), MDC.get("traceId")));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> serverError(Exception ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiError(Instant.now(), req.getRequestURI(), "Server Error", ex.getMessage(), MDC.get("traceId")));
  }
}
