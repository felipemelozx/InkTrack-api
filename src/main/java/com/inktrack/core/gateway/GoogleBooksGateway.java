package com.inktrack.core.gateway;

import com.inktrack.core.usecases.book.GoogleBooksVolume;
import com.inktrack.core.usecases.book.SearchBooksOutput;

import java.util.Optional;

public interface GoogleBooksGateway {

  SearchBooksOutput searchBooks(String query);

  Optional<GoogleBooksVolume> getVolumeById(String volumeId);
}
