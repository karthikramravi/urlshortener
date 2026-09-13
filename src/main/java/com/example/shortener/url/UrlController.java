package com.example.shortener.url;

import com.example.shortener.url.UrlDtos.*;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlController {
  private final UrlShortenerService service;
  public UrlController(UrlShortenerService service) { this.service = service; }
  @PostMapping("/api/v1/urls")
  ResponseEntity<CreateResponse> create(@Valid @RequestBody CreateRequest request) {
    CreateResponse response = service.create(request);
    return ResponseEntity.created(URI.create(response.shortUrl())).body(response);
  }
  @GetMapping("/api/v1/urls/{code}/analytics") AnalyticsResponse analytics(@PathVariable String code) { return service.analytics(code); }
  @GetMapping("/{code:[A-Za-z0-9]{7}}") ResponseEntity<Void> redirect(@PathVariable String code) {
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(service.resolve(code))).build();
  }
}
