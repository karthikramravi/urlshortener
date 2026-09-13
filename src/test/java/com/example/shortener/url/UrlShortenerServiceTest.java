package com.example.shortener.url;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.shortener.url.UrlDtos.CreateRequest;
import com.example.shortener.url.UrlExceptions.*;
import java.time.*;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.mockito.*;

class UrlShortenerServiceTest {
  @Mock ShortUrlRepository repository; @Mock CodeGenerator generator;
  private final Instant now = Instant.parse("2026-09-13T12:00:00Z");
  UrlShortenerService service;
  @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); service = new UrlShortenerService(repository, generator, Clock.fixed(now, ZoneOffset.UTC), "http://sho.rt"); }
  @Test void rejectsNonHttpUrl() { assertThatThrownBy(() -> service.create(new CreateRequest("file:///etc/passwd", null))).isInstanceOf(InvalidUrl.class); }
  @Test void resolvesAndRecordsClick() {
    ShortUrl entity = new ShortUrl("Ab12xYz", "https://example.com", now, now.plusSeconds(60));
    when(repository.findByShortCode("Ab12xYz")).thenReturn(Optional.of(entity));
    assertThat(service.resolve("Ab12xYz")).isEqualTo("https://example.com"); verify(repository).recordClick(entity.getId(), now);
  }
  @Test void expiredLinkReturnsGoneSemantics() {
    when(repository.findByShortCode("Ab12xYz")).thenReturn(Optional.of(new ShortUrl("Ab12xYz", "https://example.com", now.minusSeconds(120), now)));
    assertThatThrownBy(() -> service.resolve("Ab12xYz")).isInstanceOf(Expired.class); verify(repository, never()).recordClick(any(), any());
  }
  @Test void missingCodeIsNotFound() { when(repository.findByShortCode("Ab12xYz")).thenReturn(Optional.empty()); assertThatThrownBy(() -> service.analytics("Ab12xYz")).isInstanceOf(NotFound.class); }
}
