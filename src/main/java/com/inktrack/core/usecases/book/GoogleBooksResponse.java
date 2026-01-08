package com.inktrack.core.usecases.book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleBooksResponse(
    String id,
    VolumeInfo volumeInfo
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VolumeInfo(
        String title,
        List<String> authors,
        Integer pageCount,
        ImageLinks imageLinks
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ImageLinks(
        String thumbnail
    ) {
    }
}
