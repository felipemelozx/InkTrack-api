package com.inktrack.infrastructure.gateway;

import com.inktrack.core.gateway.GoogleBooksGateway;
import com.inktrack.core.usecases.book.GoogleBooksItemResponse;
import com.inktrack.core.usecases.book.GoogleBooksSearchResponse;
import com.inktrack.core.usecases.book.GoogleBooksVolume;
import com.inktrack.core.usecases.book.SearchBooksOutput;
import com.inktrack.infrastructure.config.CacheConfig;
import com.inktrack.infrastructure.config.GoogleBooksConfig;
import com.inktrack.infrastructure.exception.ExternalApiException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
public class GoogleBooksGatewayImpl implements GoogleBooksGateway {

  private final RestClient restClient;

  public GoogleBooksGatewayImpl(GoogleBooksConfig config) {
    this.restClient = RestClient.builder()
        .baseUrl(config.getBaseUrl())
        .build();
  }


  @Override
  @Cacheable(value = CacheConfig.GOOGLE_BOOKS_CACHE, key = "'search:' + #query")
  public SearchBooksOutput searchBooks(String query) {

    GoogleBooksSearchResponse response =
        restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/volumes")
                .queryParam("q", query)
                .queryParam("maxResults", 10)
                .queryParam("printType", "books")
                .build()
            )
            .retrieve()
            .body(GoogleBooksSearchResponse.class);

    if (response == null) {
      return new SearchBooksOutput(0, List.of());
    }

    List<GoogleBooksVolume> books =
        response.items() == null
            ? List.of()
            : response.items()
            .stream()
            .map(this::toDomain)
            .toList();

    return new SearchBooksOutput(books.size(), books);
  }

  @Override
  @Cacheable(value = CacheConfig.GOOGLE_BOOKS_CACHE, key = "'volume:' + #volumeId")
  public Optional<GoogleBooksVolume> getVolumeById(String volumeId) {
    GoogleBooksItemResponse response =
        restClient.get()
            .uri("/volumes/{id}", volumeId)
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                (req, res) -> {
                  throw new ExternalApiException(
                      "Livro não encontrado no Google Books",
                      HttpStatus.NOT_FOUND.value()
                  );
                }
            )
            .onStatus(
                HttpStatusCode::is5xxServerError,
                (req, res) -> {
                  throw new ExternalApiException(
                      "Erro no Google Books",
                      HttpStatus.BAD_GATEWAY.value()
                  );
                }
            )
            .body(GoogleBooksItemResponse.class);

    return Optional.ofNullable(response).map(this::toDomain);
  }

  private GoogleBooksVolume toDomain(GoogleBooksItemResponse item) {
    return new GoogleBooksVolume(
        item.id(),
        item.volumeInfo().title(),
        item.volumeInfo().authors(),
        item.volumeInfo().pageCount(),
        item.volumeInfo().imageLinks() != null
            ? item.volumeInfo().imageLinks().thumbnail()
            : null
    );
  }
}

