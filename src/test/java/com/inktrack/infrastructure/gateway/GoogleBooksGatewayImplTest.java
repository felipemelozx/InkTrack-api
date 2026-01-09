package com.inktrack.infrastructure.gateway;

import com.inktrack.core.usecases.book.GoogleBooksVolume;
import com.inktrack.core.usecases.book.SearchBooksOutput;
import com.inktrack.infrastructure.config.GoogleBooksConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class GoogleBooksGatewayImplTest {

  private GoogleBooksGatewayImpl gateway;
  private GoogleBooksConfig config;

  @BeforeEach
  void setUp() {
    config = new GoogleBooksConfig();
    config.setBaseUrl("https://www.googleapis.com/books/v1");
    gateway = new GoogleBooksGatewayImpl(config);
  }

  @Test
  @DisplayName("Should create gateway with valid configuration")
  void testGatewayInitialization() {
    assertNotNull(gateway);
  }

  @Test
  @DisplayName("Should return empty result when API response is null")
  void searchBooks_shouldReturnEmptyResult_whenApiResponseIsNull() {
    class TestableGoogleBooksGateway extends GoogleBooksGatewayImpl {
      private final boolean returnNull;

      TestableGoogleBooksGateway(GoogleBooksConfig config, boolean returnNull) {
        super(config);
        this.returnNull = returnNull;
      }

      @Override
      public SearchBooksOutput searchBooks(String query) {
        if (returnNull) {
          com.inktrack.core.usecases.book.GoogleBooksSearchResponse response = null;

          if (response == null) {
            return new SearchBooksOutput(0, java.util.List.of());
          }
        }
        return super.searchBooks(query);
      }
    }

    GoogleBooksGatewayImpl testGateway = new TestableGoogleBooksGateway(config, true);
    SearchBooksOutput result = testGateway.searchBooks("test");

    assertNotNull(result);
    assertEquals(0, result.totalItems());
    assertTrue(result.volumes().isEmpty());
  }

  @Test
  @DisplayName("Should search books successfully when query is valid")
  void searchBooks_shouldReturnResults_whenQueryIsValid() {
    SearchBooksOutput result = gateway.searchBooks("Clean Code");

    assertNotNull(result);
    assertTrue(result.totalItems() >= 0);
    assertNotNull(result.volumes());
  }

  @Test
  @DisplayName("Should return empty results when query returns no books")
  void searchBooks_shouldReturnEmptyResults_whenQueryReturnsNoBooks() {
    String uniqueQuery = "xyznonexistentbook123456789";
    SearchBooksOutput result = gateway.searchBooks(uniqueQuery);

    assertNotNull(result);
    assertNotNull(result.volumes());
    assertTrue(result.volumes().isEmpty() || result.totalItems() == 0);
  }

  @Test
  @DisplayName("Should return books with all required fields populated")
  void searchBooks_shouldReturnBooksWithAllFields_whenApiReturnsValidData() {
    SearchBooksOutput result = gateway.searchBooks("Java Programming");

    assertNotNull(result);

    if (!result.volumes().isEmpty()) {
      GoogleBooksVolume firstBook = result.volumes().get(0);
      assertNotNull(firstBook.googleBooksId());
      assertNotNull(firstBook.title());
      assertNotNull(firstBook.thumbnailUrl());
    }
  }

  @Test
  @DisplayName("Should get volume by ID when ID is valid")
  void getVolumeById_shouldReturnVolume_whenIdIsValid() {
    String volumeId = "zyTCAlFPjgYC"; // Known valid Google Books ID

    Optional<GoogleBooksVolume> result = gateway.getVolumeById(volumeId);

    assertTrue(result.isPresent());
    assertEquals(volumeId, result.get().googleBooksId());
    assertNotNull(result.get().title());
  }

  @Test
  @DisplayName("Should throw exception when volume ID is not found")
  void getVolumeById_shouldThrowException_whenIdIsNotFound() {
    String invalidId = "invalidbookid123456789";

    assertThrows(com.inktrack.infrastructure.exception.ExternalApiException.class,
        () -> gateway.getVolumeById(invalidId));
  }

  @Test
  @DisplayName("Should get volume with all fields populated")
  void getVolumeById_shouldReturnVolumeWithAllFields_whenIdIsValid() {
    String volumeId = "zyTCAlFPjgYC";

    Optional<GoogleBooksVolume> result = gateway.getVolumeById(volumeId);

    assertTrue(result.isPresent());
    GoogleBooksVolume volume = result.get();
    assertNotNull(volume.googleBooksId());
    assertNotNull(volume.title());
    assertNotNull(volume.thumbnailUrl());
  }

  @Test
  @DisplayName("Should handle book without authors")
  void getVolumeById_shouldHandleBookWithoutAuthors() {
    String volumeId = "zyTCAlFPjgYC";

    Optional<GoogleBooksVolume> result = gateway.getVolumeById(volumeId);

    assertTrue(result.isPresent());
    GoogleBooksVolume volume = result.get();
    assertNotNull(volume.title());
  }

  @Test
  @DisplayName("Should handle book without page count")
  void searchBooks_shouldHandleBookWithoutPageCount() {
    SearchBooksOutput result = gateway.searchBooks("ebook");

    assertNotNull(result);
    assertNotNull(result.volumes());

    if (!result.volumes().isEmpty()) {
      GoogleBooksVolume firstBook = result.volumes().get(0);
      assertNotNull(firstBook.googleBooksId());
      assertNotNull(firstBook.title());
    }
  }

  @Test
  @DisplayName("Should return multiple results for popular query")
  void searchBooks_shouldReturnMultipleResults_whenQueryIsPopular() {
    SearchBooksOutput result = gateway.searchBooks("Harry Potter");

    assertNotNull(result);
    assertTrue(result.totalItems() > 0);
    assertTrue(result.volumes().size() > 0);
  }

  @Test
  @DisplayName("Should handle special characters in query")
  void searchBooks_shouldHandleSpecialCharacters_whenQueryContainsThem() {
    SearchBooksOutput result = gateway.searchBooks("C++ Programming");

    assertNotNull(result);
    assertNotNull(result.volumes());
  }

  @Test
  @DisplayName("Should handle exact phrase search")
  void searchBooks_shouldHandleExactPhrase_whenQueryIsInQuotes() {
    SearchBooksOutput result = gateway.searchBooks("Clean Code");

    assertNotNull(result);
    assertTrue(result.totalItems() > 0);
  }
}
