package com.fountlinedigital.backend.controller;

import com.fountlinedigital.backend.dto.ClientCreateRequest;
import com.fountlinedigital.backend.dto.ClientUpdateRequest;
import com.fountlinedigital.backend.dto.EmployeeCreateRequest;
import com.fountlinedigital.backend.dto.EmployeeUpdateRequest;
import com.fountlinedigital.backend.dto.PersonResponse;
import com.fountlinedigital.backend.dto.PersonReportRow;
import com.fountlinedigital.backend.dto.ReportResponse;
import com.fountlinedigital.backend.service.PersonService;
import com.fountlinedigital.backend.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/people")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService service;
    private final ReportService reportService;

    @GetMapping
    public List<PersonResponse> search(@RequestParam(required = false) String q) {
        return service.search(q);
    }

    @PostMapping("/employees")
    public PersonResponse createEmployee(@Valid @RequestBody EmployeeCreateRequest req) {
        return service.createEmployee(req);
    }

    @PutMapping("/employees/{id}")
    public PersonResponse updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeUpdateRequest req) {
        return service.updateEmployee(id, req);
    }

    @PostMapping("/clients")
    public PersonResponse createClient(@Valid @RequestBody ClientCreateRequest req) {
        return service.createClient(req);
    }

    @PutMapping("/clients/{id}")
    public PersonResponse updateClient(@PathVariable Long id, @Valid @RequestBody ClientUpdateRequest req) {
        return service.updateClient(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/report")
    public ReportResponse<PersonReportRow> report() {
        return reportService.peopleSnapshot();
    }
}