package com.inktrack.core.usecases.book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleBooksSearchResponse(
    List<GoogleBooksItemResponse> items
) {
}
