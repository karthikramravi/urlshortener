package com.example.shortener.url;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest @AutoConfigureMockMvc
class UrlControllerIT {
  @Autowired MockMvc mvc;
  @Test void endToEndCreateRedirectAndAnalytics() throws Exception {
    String response = mvc.perform(post("/api/v1/urls").contentType(MediaType.APPLICATION_JSON).content("{\"originalUrl\":\"https://example.com/products/123\",\"expiresInDays\":30}"))
        .andExpect(status().isCreated()).andExpect(jsonPath("$.shortCode", hasLength(7))).andReturn().getResponse().getContentAsString();
    String code = response.replaceAll(".*\\\"shortCode\\\":\\\"([^\\\"]+).*", "$1");
    mvc.perform(get("/" + code)).andExpect(status().isFound()).andExpect(header().string("Location", "https://example.com/products/123"));
    mvc.perform(get("/api/v1/urls/" + code + "/analytics")).andExpect(status().isOk()).andExpect(jsonPath("$.clickCount").value(1));
  }
  @Test void malformedUrlReturnsStructured400() throws Exception {
    mvc.perform(post("/api/v1/urls").contentType(MediaType.APPLICATION_JSON).content("{\"originalUrl\":\"javascript:alert(1)\"}"))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists());
  }
}
