package com.example.shortener.url;

public final class UrlExceptions {
  private UrlExceptions() {}
  public static class NotFound extends RuntimeException { public NotFound(String code) { super("Short code not found: " + code); } }
  public static class Expired extends RuntimeException { public Expired(String code) { super("Short URL has expired: " + code); } }
  public static class InvalidUrl extends RuntimeException { public InvalidUrl(String message) { super(message); } }
  public static class CodeGenerationFailed extends RuntimeException { public CodeGenerationFailed() { super("Unable to allocate a unique short code"); } }
}
