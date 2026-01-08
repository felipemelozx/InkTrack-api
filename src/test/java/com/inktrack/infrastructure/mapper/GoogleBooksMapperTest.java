package com.inktrack.infrastructure.mapper;

import com.inktrack.core.usecases.book.GoogleBooksVolume;
import com.inktrack.core.usecases.book.SearchBooksOutput;
import com.inktrack.infrastructure.dtos.book.BookSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GoogleBooksMapperTest {

  private GoogleBooksMapper googleBooksMapper;

  @BeforeEach
  void setUp() {
    googleBooksMapper = new GoogleBooksMapper();
  }

  @Test
  void toSearchResponse_withValidOutput_mapsCorrectly() {
    List<GoogleBooksVolume> volumes = List.of(
        new GoogleBooksVolume(
            "testId123",
            "Test Book Title",
            List.of("Author One", "Author Two"),
            300,
            "http://example.com/thumb.jpg"
        )
    );

    SearchBooksOutput output = new SearchBooksOutput(1, volumes);

    BookSearchResponse response = googleBooksMapper.toSearchResponse(output);

    assertNotNull(response);
    assertEquals(1, response.totalItems());
    assertEquals(1, response.volumes().size());

    BookSearchResponse.BookSearchItem item = response.volumes().get(0);
    assertEquals("testId123", item.googleBooksId());
    assertEquals("Test Book Title", item.title());
    assertEquals("Author One, Author Two", item.author());
    assertEquals(300, item.totalPages());
    assertEquals("http://example.com/thumb.jpg", item.thumbnailUrl());
  }

  @Test
  void toSearchResponse_withNullAuthors_mapsToEmptyString() {
    GoogleBooksVolume volume = new GoogleBooksVolume(
        "testId123",
        "Test Book Title",
        null,
        300,
        "http://example.com/thumb.jpg"
    );

    SearchBooksOutput output = new SearchBooksOutput(1, List.of(volume));

    BookSearchResponse response = googleBooksMapper.toSearchResponse(output);

    assertEquals("", response.volumes().get(0).author());
  }

  @Test
  void toSearchResponse_withEmptyAuthors_mapsToEmptyString() {
    GoogleBooksVolume volume = new GoogleBooksVolume(
        "testId123",
        "Test Book Title",
        List.of(),
        300,
        "http://example.com/thumb.jpg"
    );

    SearchBooksOutput output = new SearchBooksOutput(1, List.of(volume));

    BookSearchResponse response = googleBooksMapper.toSearchResponse(output);

    assertEquals("", response.volumes().get(0).author());
  }

  @Test
  void toSearchResponse_withMultipleVolumes_mapsAll() {
    List<GoogleBooksVolume> volumes = List.of(
        new GoogleBooksVolume(
            "id1",
            "Book 1",
            List.of("Author 1"),
            100,
            "http://example.com/thumb1.jpg"
        ),
        new GoogleBooksVolume(
            "id2",
            "Book 2",
            List.of("Author 2"),
            200,
            "http://example.com/thumb2.jpg"
        )
    );

    SearchBooksOutput output = new SearchBooksOutput(2, volumes);

    BookSearchResponse response = googleBooksMapper.toSearchResponse(output);

    assertEquals(2, response.totalItems());
    assertEquals(2, response.volumes().size());
    assertEquals("Book 1", response.volumes().get(0).title());
    assertEquals("Book 2", response.volumes().get(1).title());
  }

  @Test
  void toSearchResponse_withNullThumbnailUrl_mapsCorrectly() {
    GoogleBooksVolume volume = new GoogleBooksVolume(
        "testId123",
        "Test Book Title",
        List.of("Author One"),
        300,
        null
    );

    SearchBooksOutput output = new SearchBooksOutput(1, List.of(volume));

    BookSearchResponse response = googleBooksMapper.toSearchResponse(output);

    // thumbnailUrl can be null
    assertEquals("testId123", response.volumes().get(0).googleBooksId());
    assertEquals("Test Book Title", response.volumes().get(0).title());
  }
}
