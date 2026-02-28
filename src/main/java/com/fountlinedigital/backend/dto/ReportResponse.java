package com.fountlinedigital.backend.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ReportResponse<T>(
        String title,
        OffsetDateTime generatedAt,
        List<T> rows
) {}