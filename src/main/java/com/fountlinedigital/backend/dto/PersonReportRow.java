package com.fountlinedigital.backend.dto;

public record PersonReportRow(
        Long id,
        String type,
        String code,
        String firstName,
        String lastName,
        String email,
        String roleOrCompany
) {}