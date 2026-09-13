package com.example.shortener.url;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class CodeGenerator {
  private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
  private final SecureRandom random = new SecureRandom();
  public String generate(int length) {
    var result = new char[length];
    for (int i = 0; i < length; i++) result[i] = ALPHABET[random.nextInt(ALPHABET.length)];
    return new String(result);
  }
}
