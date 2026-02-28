package com.fountlinedigital.backend.dto;

import java.time.OffsetDateTime;

public record InventoryResponse(
        Long id,
        String name,
        String sku,
        int quantity,
        int reorderLevel,
        OffsetDateTime updatedAt
) {}