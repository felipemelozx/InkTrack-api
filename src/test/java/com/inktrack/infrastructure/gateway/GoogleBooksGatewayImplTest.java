package com.inktrack.infrastructure.gateway;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.inktrack.infrastructure.config.GoogleBooksConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GoogleBooksGatewayImplTest {

  private GoogleBooksConfig config;
  private GoogleBooksGatewayImpl gateway;

  @BeforeEach
  void setUp() {
    config = new GoogleBooksConfig();
    config.setBaseUrl("https://www.googleapis.com/books/v1");
    gateway = new GoogleBooksGatewayImpl(config);
  }

  @Test
  void testGatewayInitialization() {
    assertNotNull(gateway);
  }
}
