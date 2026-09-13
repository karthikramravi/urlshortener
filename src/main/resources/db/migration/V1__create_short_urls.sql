CREATE TABLE short_urls (
  id BIGSERIAL PRIMARY KEY,
  short_code VARCHAR(12) NOT NULL UNIQUE,
  original_url VARCHAR(2048) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  expires_at TIMESTAMPTZ NULL,
  click_count BIGINT NOT NULL DEFAULT 0,
  last_accessed_at TIMESTAMPTZ NULL
);
CREATE UNIQUE INDEX idx_short_urls_code ON short_urls(short_code);
