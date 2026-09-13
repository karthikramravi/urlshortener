package com.example.shortener.url;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
  Optional<ShortUrl> findByShortCode(String shortCode);
  @Modifying(clearAutomatically = true)
  @Query("update ShortUrl s set s.clickCount = s.clickCount + 1, s.lastAccessedAt = :at where s.id = :id")
  int recordClick(@Param("id") Long id, @Param("at") Instant at);
}
