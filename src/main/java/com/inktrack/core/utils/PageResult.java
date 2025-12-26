package com.inktrack.core.utils;

import java.util.List;

public record PageResult<T>(
    int pageSize,
    int totalPages,
    int currentPage,
    List<T> data
) {
}
