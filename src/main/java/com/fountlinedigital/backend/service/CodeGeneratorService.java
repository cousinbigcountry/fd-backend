package com.fountlinedigital.backend.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class CodeGeneratorService {
    private final SecureRandom rng = new SecureRandom();

    public String employeeCode() {
        return "FDE" + (100 + rng.nextInt(900)); // FDE100-FDE999
    }

    public String clientCode() {
        return "FDC" + (100 + rng.nextInt(900)); // FDC100-FDC999
    }
}