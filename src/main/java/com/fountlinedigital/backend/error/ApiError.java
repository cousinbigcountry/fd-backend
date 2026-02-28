package com.fountlinedigital.backend.error;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
        String message,
        OffsetDateTime timestamp,
        List<FieldError> fieldErrors
) {
    public record FieldError(String field, String error) {}
}