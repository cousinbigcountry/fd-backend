package com.fountlinedigital.backend.controller;

import com.fountlinedigital.backend.dto.InventoryCreateRequest;
import com.fountlinedigital.backend.dto.InventoryReportRow;
import com.fountlinedigital.backend.dto.InventoryResponse;
import com.fountlinedigital.backend.dto.InventoryUpdateRequest;
import com.fountlinedigital.backend.dto.ReportResponse;
import com.fountlinedigital.backend.service.InventoryService;
import com.fountlinedigital.backend.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;
    private final ReportService reportService;

    @GetMapping
    public List<InventoryResponse> search(@RequestParam(required = false) String q) {
        return service.search(q);
    }

    @GetMapping("/{id}")
    public InventoryResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public InventoryResponse create(@Valid @RequestBody InventoryCreateRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public InventoryResponse update(@PathVariable Long id, @Valid @RequestBody InventoryUpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/report")
    public ReportResponse<InventoryReportRow> report() {
        return reportService.inventorySnapshot();
    }
}