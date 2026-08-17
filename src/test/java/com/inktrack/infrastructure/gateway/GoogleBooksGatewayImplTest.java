package com.inktrack.infrastructure.gateway;

import com.inktrack.core.usecases.book.GoogleBooksItemResponse;
import com.inktrack.core.usecases.book.GoogleBooksItemResponse.ImageLinks;
import com.inktrack.core.usecases.book.GoogleBooksItemResponse.VolumeInfo;
import com.inktrack.core.usecases.book.GoogleBooksSearchResponse;
import com.inktrack.core.usecases.book.GoogleBooksVolume;
import com.inktrack.core.usecases.book.SearchBooksOutput;
import com.inktrack.infrastructure.config.GoogleBooksConfig;
import com.inktrack.infrastructure.exception.ExternalApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class GoogleBooksGatewayImplTest {

  private GoogleBooksConfig config;

  @BeforeEach
  void setUp() {
    config = new GoogleBooksConfig();
    config.setBaseUrl("https://www.googleapis.com/books/v1");
  }

  @Test
  @DisplayName("Should create gateway with valid configuration")
  void testGatewayInitialization() {
    GoogleBooksGatewayImpl gateway = new GoogleBooksGatewayImpl(config);
    assertNotNull(gateway);
  }

  @Test
  @DisplayName("Should return empty result when API response is null")
  void searchBooks_shouldReturnEmptyResult_whenApiResponseIsNull() {
    GoogleBooksGatewayImpl gateway = createGatewayForSearch(null);
    SearchBooksOutput result = gateway.searchBooks("test");

    assertNotNull(result);
    assertEquals(0, result.totalItems());
    assertTrue(result.volumes().isEmpty());
  }

  @Test
  @DisplayName("Should search books successfully when query is valid")
  void searchBooks_shouldReturnResults_whenQueryIsValid() {
    GoogleBooksSearchResponse response = new GoogleBooksSearchResponse(
        List.of(new GoogleBooksItemResponse("id1",
            new VolumeInfo("Clean Code", List.of("Robert C. Martin"), 464, null)))
    );

    GoogleBooksGatewayImpl gateway = createGatewayForSearch(response);
    SearchBooksOutput result = gateway.searchBooks("Clean Code");

    assertNotNull(result);
    assertTrue(result.totalItems() > 0);
    assertFalse(result.volumes().isEmpty());
    assertEquals("id1", result.volumes().get(0).googleBooksId());
  }

  @Test
  @DisplayName("Should return empty results when query returns no books")
  void searchBooks_shouldReturnEmptyResults_whenQueryReturnsNoBooks() {
    GoogleBooksSearchResponse response = new GoogleBooksSearchResponse(List.of());

    GoogleBooksGatewayImpl gateway = createGatewayForSearch(response);
    SearchBooksOutput result = gateway.searchBooks("xyznonexistentbook123456789");

    assertNotNull(result);
    assertTrue(result.volumes().isEmpty());
    assertEquals(0, result.totalItems());
  }

  @Test
  @DisplayName("Should return books with all required fields populated")
  void searchBooks_shouldReturnBooksWithAllFields_whenApiReturnsValidData() {
    GoogleBooksSearchResponse response = new GoogleBooksSearchResponse(
        List.of(new GoogleBooksItemResponse("id1",
            new VolumeInfo("Java Programming", List.of("Author"), 300,
                new ImageLinks("http://thumb.url"))))
    );

    GoogleBooksGatewayImpl gateway = createGatewayForSearch(response);
    SearchBooksOutput result = gateway.searchBooks("Java Programming");

    assertNotNull(result);
    assertFalse(result.volumes().isEmpty());
    GoogleBooksVolume firstBook = result.volumes().get(0);
    assertNotNull(firstBook.googleBooksId());
    assertNotNull(firstBook.title());
    assertNotNull(firstBook.thumbnailUrl());
  }

  @Test
  @DisplayName("Should get volume by ID when ID is valid")
  void getVolumeById_shouldReturnVolume_whenIdIsValid() {
    GoogleBooksItemResponse response = new GoogleBooksItemResponse("zyTCAlFPjgYC",
        new VolumeInfo("Clean Code", List.of("Robert C. Martin"), 464, null));

    GoogleBooksGatewayImpl gateway = createGatewayForVolume(response);
    Optional<GoogleBooksVolume> result = gateway.getVolumeById("zyTCAlFPjgYC");

    assertTrue(result.isPresent());
    assertEquals("zyTCAlFPjgYC", result.get().googleBooksId());
    assertNotNull(result.get().title());
  }

  @Test
  @DisplayName("Should throw exception when volume ID is not found")
  void getVolumeById_shouldThrowException_whenIdIsNotFound() {
    RestClient restClient = mock(RestClient.class);
    var uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
    var responseSpec = mock(RestClient.ResponseSpec.class);

    given(restClient.get()).willReturn(uriSpec);
    given(uriSpec.uri(anyString(), any(Object[].class))).willReturn(uriSpec);
    given(uriSpec.retrieve()).willReturn(responseSpec);
    given(responseSpec.onStatus(any(), any())).willReturn(responseSpec);
    given(responseSpec.body(GoogleBooksItemResponse.class))
        .willThrow(new ExternalApiException("Livro não encontrado no Google Books", 404));

    GoogleBooksGatewayImpl gateway = injectRestClient(restClient);

    assertThrows(ExternalApiException.class,
        () -> gateway.getVolumeById("invalidbookid123456789"));
  }

  @Test
  @DisplayName("Should get volume with all fields populated")
  void getVolumeById_shouldReturnVolumeWithAllFields_whenIdIsValid() {
    GoogleBooksItemResponse response = new GoogleBooksItemResponse("zyTCAlFPjgYC",
        new VolumeInfo("Clean Code", List.of("Robert C. Martin"), 464,
            new ImageLinks("http://thumb.url")));

    GoogleBooksGatewayImpl gateway = createGatewayForVolume(response);
    Optional<GoogleBooksVolume> result = gateway.getVolumeById("zyTCAlFPjgYC");

    assertTrue(result.isPresent());
    GoogleBooksVolume volume = result.get();
    assertNotNull(volume.googleBooksId());
    assertNotNull(volume.title());
    assertNotNull(volume.thumbnailUrl());
  }

  @Test
  @DisplayName("Should handle book without authors")
  void getVolumeById_shouldHandleBookWithoutAuthors() {
    GoogleBooksItemResponse response = new GoogleBooksItemResponse("zyTCAlFPjgYC",
        new VolumeInfo("Clean Code", null, 464, null));

    GoogleBooksGatewayImpl gateway = createGatewayForVolume(response);
    Optional<GoogleBooksVolume> result = gateway.getVolumeById("zyTCAlFPjgYC");

    assertTrue(result.isPresent());
    GoogleBooksVolume volume = result.get();
    assertNotNull(volume.title());
    assertNull(volume.authors());
  }

  @Test
  @DisplayName("Should handle book without page count")
  void searchBooks_shouldHandleBookWithoutPageCount() {
    GoogleBooksSearchResponse response = new GoogleBooksSearchResponse(
        List.of(new GoogleBooksItemResponse("id1",
            new VolumeInfo("Ebook Title", List.of("Author"), null, null)))
    );

    GoogleBooksGatewayImpl gateway = createGatewayForSearch(response);
    SearchBooksOutput result = gateway.searchBooks("ebook");

    assertNotNull(result);
    assertFalse(result.volumes().isEmpty());
    GoogleBooksVolume firstBook = result.volumes().get(0);
    assertNotNull(firstBook.googleBooksId());
    assertNotNull(firstBook.title());
    assertNull(firstBook.pageCount());
  }

  @Test
  @DisplayName("Should return multiple results for popular query")
  void searchBooks_shouldReturnMultipleResults_whenQueryIsPopular() {
    GoogleBooksSearchResponse response = new GoogleBooksSearchResponse(
        List.of(
            new GoogleBooksItemResponse("id1",
                new VolumeInfo("Harry Potter 1", List.of("J.K. Rowling"), 300, null)),
            new GoogleBooksItemResponse("id2",
                new VolumeInfo("Harry Potter 2", List.of("J.K. Rowling"), 350, null))
        )
    );

    GoogleBooksGatewayImpl gateway = createGatewayForSearch(response);
    SearchBooksOutput result = gateway.searchBooks("Harry Potter");

    assertNotNull(result);
    assertTrue(result.totalItems() > 0);
    assertFalse(result.volumes().isEmpty());
  }

  @Test
  @DisplayName("Should handle special characters in query")
  void searchBooks_shouldHandleSpecialCharacters_whenQueryContainsThem() {
    GoogleBooksSearchResponse response = new GoogleBooksSearchResponse(
        List.of(new GoogleBooksItemResponse("id1",
            new VolumeInfo("C++ Programming", List.of("Author"), 400, null)))
    );

    GoogleBooksGatewayImpl gateway = createGatewayForSearch(response);
    SearchBooksOutput result = gateway.searchBooks("C++ Programming");

    assertNotNull(result);
    assertNotNull(result.volumes());
  }

  @Test
  @DisplayName("Should handle exact phrase search")
  void searchBooks_shouldHandleExactPhrase_whenQueryIsInQuotes() {
    GoogleBooksSearchResponse response = new GoogleBooksSearchResponse(
        List.of(new GoogleBooksItemResponse("id1",
            new VolumeInfo("Clean Code", List.of("Robert C. Martin"), 464, null)))
    );

    GoogleBooksGatewayImpl gateway = createGatewayForSearch(response);
    SearchBooksOutput result = gateway.searchBooks("Clean Code");

    assertNotNull(result);
    assertTrue(result.totalItems() > 0);
  }

  private GoogleBooksGatewayImpl createGatewayForSearch(GoogleBooksSearchResponse response) {
    RestClient restClient = mock(RestClient.class);
    var uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
    var responseSpec = mock(RestClient.ResponseSpec.class);

    given(restClient.get()).willReturn(uriSpec);
    given(uriSpec.uri(any(Function.class))).willReturn(uriSpec);
    given(uriSpec.retrieve()).willReturn(responseSpec);
    given(responseSpec.body(GoogleBooksSearchResponse.class)).willReturn(response);

    return injectRestClient(restClient);
  }

  private GoogleBooksGatewayImpl createGatewayForVolume(GoogleBooksItemResponse response) {
    RestClient restClient = mock(RestClient.class);
    var uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
    var responseSpec = mock(RestClient.ResponseSpec.class);

    given(restClient.get()).willReturn(uriSpec);
    given(uriSpec.uri(anyString(), any(Object[].class))).willReturn(uriSpec);
    given(uriSpec.retrieve()).willReturn(responseSpec);
    given(responseSpec.onStatus(any(), any())).willReturn(responseSpec);
    given(responseSpec.body(GoogleBooksItemResponse.class)).willReturn(response);

    return injectRestClient(restClient);
  }

  private GoogleBooksGatewayImpl injectRestClient(RestClient restClient) {
    return new GoogleBooksGatewayImpl(restClient);
  }
}
