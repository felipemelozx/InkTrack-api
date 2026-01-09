package com.inktrack.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google.books")
public class GoogleBooksConfig {

  private String apiKey;
  private String baseUrl = "https://www.googleapis.com/books/v1";
  private int maxResults = 10;
  private int timeoutMs = 5000;
  private long cacheMaxSize = 100;
  private long cacheExpirationMinutes = 5;

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public int getMaxResults() {
    return maxResults;
  }

  public void setMaxResults(int maxResults) {
    this.maxResults = maxResults;
  }

  public int getTimeoutMs() {
    return timeoutMs;
  }

  public void setTimeoutMs(int timeoutMs) {
    this.timeoutMs = timeoutMs;
  }

  public long getCacheMaxSize() {
    return cacheMaxSize;
  }

  public void setCacheMaxSize(long cacheMaxSize) {
    this.cacheMaxSize = cacheMaxSize;
  }

  public long getCacheExpirationMinutes() {
    return cacheExpirationMinutes;
  }

  public void setCacheExpirationMinutes(long cacheExpirationMinutes) {
    this.cacheExpirationMinutes = cacheExpirationMinutes;
  }
}
