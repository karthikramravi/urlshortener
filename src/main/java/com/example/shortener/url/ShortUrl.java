package com.example.shortener.url;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "short_urls", indexes = @Index(name = "idx_short_urls_code", columnList = "short_code", unique = true))
public class ShortUrl {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(name = "short_code", nullable = false, unique = true, length = 12) private String shortCode;
  @Column(name = "original_url", nullable = false, length = 2048) private String originalUrl;
  @Column(name = "created_at", nullable = false) private Instant createdAt;
  @Column(name = "expires_at") private Instant expiresAt;
  @Column(name = "click_count", nullable = false) private long clickCount;
  @Column(name = "last_accessed_at") private Instant lastAccessedAt;
  protected ShortUrl() {}
  public ShortUrl(String code, String url, Instant createdAt, Instant expiresAt) {
    this.shortCode = code; this.originalUrl = url; this.createdAt = createdAt; this.expiresAt = expiresAt;
  }
  public Long getId() { return id; }
  public String getShortCode() { return shortCode; }
  public String getOriginalUrl() { return originalUrl; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getExpiresAt() { return expiresAt; }
  public long getClickCount() { return clickCount; }
  public Instant getLastAccessedAt() { return lastAccessedAt; }
}
