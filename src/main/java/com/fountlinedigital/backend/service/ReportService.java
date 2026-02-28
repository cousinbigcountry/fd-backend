package com.fountlinedigital.backend.service;

import com.fountlinedigital.backend.dao.InventoryItemRepository;
import com.fountlinedigital.backend.dao.PersonRepository;
import com.fountlinedigital.backend.dto.InventoryReportRow;
import com.fountlinedigital.backend.dto.PersonReportRow;
import com.fountlinedigital.backend.dto.ReportResponse;
import com.fountlinedigital.backend.entity.Client;
import com.fountlinedigital.backend.entity.Employee;
import com.fountlinedigital.backend.entity.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final InventoryItemRepository inventoryRepo;
    private final PersonRepository personRepo;

    public ReportResponse<InventoryReportRow> inventorySnapshot() {
        var rows = inventoryRepo.findAll().stream()
                .map(i -> new InventoryReportRow(
                        i.getId(),
                        i.getName(),
                        i.getSku(),
                        i.getQuantity(),
                        i.getReorderLevel(),
                        i.getUpdatedAt()
                ))
                .toList();

        return new ReportResponse<>("Inventory Snapshot Report", OffsetDateTime.now(), rows);
    }

    public ReportResponse<PersonReportRow> peopleSnapshot() {
        var rows = personRepo.findAll().stream()
                .map(this::toPersonRow)
                .toList();

        return new ReportResponse<>("People Snapshot Report", OffsetDateTime.now(), rows);
    }

    private PersonReportRow toPersonRow(Person p) {
        if (p instanceof Employee e) {
            return new PersonReportRow(
                    e.getId(),
                    "EMPLOYEE",
                    e.getEmployeeCode(),
                    e.getFirstName(),
                    e.getLastName(),
                    e.getEmail(),
                    e.getDepartment().toString()
            );
        }
        if (p instanceof Client c) {
            return new PersonReportRow(
                    c.getId(),
                    "CLIENT",
                    c.getClientCode(),
                    c.getFirstName(),
                    c.getLastName(),
                    c.getEmail(),
                    c.getCompanyName()
            );
        }
        return new PersonReportRow(
                p.getId(),
                "PERSON",
                null,
                p.getFirstName(),
                p.getLastName(),
                p.getEmail(),
                null
        );
    }
}