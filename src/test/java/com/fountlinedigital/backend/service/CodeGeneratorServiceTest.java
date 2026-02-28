package com.fountlinedigital.backend.service;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CodeGeneratorServiceTest {

    private final CodeGeneratorService service = new CodeGeneratorService();

    @Test
    void generateCodesFormatAndUniqueness() {

        String emp = service.employeeCode();
        String cli = service.clientCode();

        assertNotNull(emp, "Employee code should not be null");
        assertNotNull(cli, "Client code should not be null");

        assertTrue(emp.matches("^FDE\\d{3}$"), "Expected employee code format FDE### but got: " + emp);
        assertTrue(cli.matches("^FDC\\d{3}$"), "Expected client code format FDC### but got: " + cli);


        Set<String> empCodes = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            empCodes.add(service.employeeCode());
        }

        assertTrue(empCodes.size() >= 45,
                "Expected at least 45 unique codes out of 50, but got: " + empCodes.size());
    }
}