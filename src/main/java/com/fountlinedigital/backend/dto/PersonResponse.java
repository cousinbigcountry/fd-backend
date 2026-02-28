package com.fountlinedigital.backend.dto;

public record PersonResponse(
        Long id,
        String type,
        String code,
        String firstName,
        String lastName,
        String email
) {}