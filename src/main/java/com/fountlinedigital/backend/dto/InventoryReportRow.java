package com.fountlinedigital.backend.dto;

import java.time.OffsetDateTime;

public record InventoryReportRow(
        Long id,
        String name,
        String sku,
        int quantity,
        int reorderLevel,
        OffsetDateTime updatedAt
) {}