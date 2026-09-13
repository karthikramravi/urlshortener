package com.example.shortener.common;

import com.example.shortener.url.UrlExceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
  public record Problem(Instant timestamp, int status, String error, String message, String path, Map<String,String> violations) {}
  @ExceptionHandler(NotFound.class) ResponseEntity<Problem> notFound(NotFound ex, HttpServletRequest r) { return problem(HttpStatus.NOT_FOUND, ex.getMessage(), r, Map.of()); }
  @ExceptionHandler(Expired.class) ResponseEntity<Problem> expired(Expired ex, HttpServletRequest r) { return problem(HttpStatus.GONE, ex.getMessage(), r, Map.of()); }
  @ExceptionHandler({InvalidUrl.class, CodeGenerationFailed.class}) ResponseEntity<Problem> domain(RuntimeException ex, HttpServletRequest r) {
    HttpStatus status = ex instanceof InvalidUrl ? HttpStatus.BAD_REQUEST : HttpStatus.SERVICE_UNAVAILABLE;
    return problem(status, ex.getMessage(), r, Map.of());
  }
  @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<Problem> validation(MethodArgumentNotValidException ex, HttpServletRequest r) {
    Map<String,String> fields = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(e -> fields.putIfAbsent(e.getField(), Objects.requireNonNullElse(e.getDefaultMessage(), "invalid")));
    return problem(HttpStatus.BAD_REQUEST, "Request validation failed", r, fields);
  }
  private ResponseEntity<Problem> problem(HttpStatus status, String message, HttpServletRequest r, Map<String,String> fields) {
    return ResponseEntity.status(status).body(new Problem(Instant.now(), status.value(), status.getReasonPhrase(), message, r.getRequestURI(), fields));
  }
}
