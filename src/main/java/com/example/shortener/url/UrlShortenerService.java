package com.example.shortener.url;

import com.example.shortener.url.UrlDtos.*;
import com.example.shortener.url.UrlExceptions.*;
import java.net.URI;
import java.time.*;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlShortenerService {
  private static final int CODE_LENGTH = 7;
  private static final int MAX_ATTEMPTS = 5;
  private final ShortUrlRepository repository; private final CodeGenerator generator; private final Clock clock; private final String baseUrl;
  public UrlShortenerService(ShortUrlRepository repository, CodeGenerator generator, Clock clock,
      @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
    this.repository = repository; this.generator = generator; this.clock = clock; this.baseUrl = baseUrl.replaceAll("/$", "");
  }
  public CreateResponse create(CreateRequest request) {
    String normalized = validate(request.originalUrl());
    Instant now = clock.instant();
    Instant expires = request.expiresInDays() == null ? null : now.plus(request.expiresInDays(), ChronoUnit.DAYS);
    for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
      String code = generator.generate(CODE_LENGTH);
      try {
        ShortUrl saved = repository.saveAndFlush(new ShortUrl(code, normalized, now, expires));
        return new CreateResponse(code, baseUrl + "/" + code, normalized, saved.getCreatedAt(), expires);
      } catch (DataIntegrityViolationException collision) {
        if (attempt == MAX_ATTEMPTS - 1) throw new CodeGenerationFailed();
      }
    }
    throw new CodeGenerationFailed();
  }
  @Transactional
  public String resolve(String code) {
    ShortUrl value = find(code);
    Instant now = clock.instant();
    if (value.getExpiresAt() != null && !now.isBefore(value.getExpiresAt())) throw new Expired(code);
    repository.recordClick(value.getId(), now);
    return value.getOriginalUrl();
  }
  @Transactional(readOnly = true)
  public AnalyticsResponse analytics(String code) {
    ShortUrl value = find(code);
    return new AnalyticsResponse(code, value.getClickCount(), value.getCreatedAt(), value.getLastAccessedAt(), value.getExpiresAt());
  }
  private ShortUrl find(String code) { return repository.findByShortCode(code).orElseThrow(() -> new NotFound(code)); }
  private String validate(String input) {
    try {
      URI uri = URI.create(input.trim());
      if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())) || uri.getHost() == null)
        throw new InvalidUrl("Only absolute HTTP/HTTPS URLs are accepted");
      if (uri.getUserInfo() != null) throw new InvalidUrl("URLs containing credentials are rejected");
      return uri.toASCIIString();
    } catch (IllegalArgumentException ex) {
      throw new InvalidUrl("Malformed URL");
    }
  }
}
