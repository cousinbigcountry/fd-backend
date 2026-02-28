package com.fountlinedigital.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record InventoryCreateRequest(
        @NotBlank String name,
        @NotBlank String sku,
        @Min(0) int quantity,
        @Min(0) int reorderLevel
) {}