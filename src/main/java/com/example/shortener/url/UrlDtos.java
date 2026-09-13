package com.example.shortener.url;

import jakarta.validation.constraints.*;
import java.time.Instant;

public final class UrlDtos {
  private UrlDtos() {}
  public record CreateRequest(@NotBlank @Size(max = 2048) String originalUrl, @Min(1) @Max(3650) Integer expiresInDays) {}
  public record CreateResponse(String shortCode, String shortUrl, String originalUrl, Instant createdAt, Instant expiresAt) {}
  public record AnalyticsResponse(String shortCode, long clickCount, Instant createdAt, Instant lastAccessedAt, Instant expiresAt) {}
}
