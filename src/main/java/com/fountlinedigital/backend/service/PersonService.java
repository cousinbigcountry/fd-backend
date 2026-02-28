package com.fountlinedigital.backend.service;

import com.fountlinedigital.backend.dao.PersonRepository;
import com.fountlinedigital.backend.dto.ClientCreateRequest;
import com.fountlinedigital.backend.dto.ClientUpdateRequest;
import com.fountlinedigital.backend.dto.EmployeeCreateRequest;
import com.fountlinedigital.backend.dto.EmployeeUpdateRequest;
import com.fountlinedigital.backend.dto.PersonResponse;
import com.fountlinedigital.backend.entity.Client;
import com.fountlinedigital.backend.entity.Department;
import com.fountlinedigital.backend.entity.Employee;
import com.fountlinedigital.backend.entity.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository repo;
    private final CodeGeneratorService codeGen;

    public List<PersonResponse> search(String q) {
        List<Person> results;
        if (q == null || q.isBlank()) results = repo.findAll();
        else {
            var byLast = repo.findByLastNameContainingIgnoreCase(q);
            var byEmail = repo.findByEmailContainingIgnoreCase(q);
            for (var p : byEmail) {
                boolean exists = byLast.stream().anyMatch(x -> x.getId().equals(p.getId()));
                if (!exists) byLast.add(p);
            }
            results = byLast;
        }
        return results.stream().map(this::toResponse).toList();
    }

    @Transactional
    public PersonResponse createEmployee(EmployeeCreateRequest req) {
        Employee e = new Employee();
        e.setFirstName(req.firstName());
        e.setLastName(req.lastName());
        e.setEmail(req.email());
        e.setDepartment(Department.valueOf(req.department()));
        e.setEmployeeCode(codeGen.employeeCode());
        return toResponse(repo.save(e));
    }

    @Transactional
    public PersonResponse createClient(ClientCreateRequest req) {
        Client c = new Client();
        c.setFirstName(req.firstName());
        c.setLastName(req.lastName());
        c.setEmail(req.email());
        c.setCompanyName(req.companyName());
        c.setClientCode(codeGen.clientCode());
        return toResponse(repo.save(c));
    }

    @Transactional
    public PersonResponse updateEmployee(Long id, EmployeeUpdateRequest req) {
        Person p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        if (!(p instanceof Employee e)) throw new IllegalArgumentException("Person is not an Employee");

        e.setFirstName(req.firstName());
        e.setLastName(req.lastName());
        e.setEmail(req.email());
        e.setDepartment(Department.valueOf(req.department()));
        return toResponse(repo.save(e));
    }

    @Transactional
    public PersonResponse updateClient(Long id, ClientUpdateRequest req) {
        Person p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Client not found"));
        if (!(p instanceof Client c)) throw new IllegalArgumentException("Person is not a Client");

        c.setFirstName(req.firstName());
        c.setLastName(req.lastName());
        c.setEmail(req.email());
        c.setCompanyName(req.companyName());
        return toResponse(repo.save(c));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Person not found");
        repo.deleteById(id);
    }

    private PersonResponse toResponse(Person p) {
        if (p instanceof Employee e) {
            return new PersonResponse(
                    e.getId(),
                    "EMPLOYEE",
                    e.getEmployeeCode(),
                    e.getFirstName(),
                    e.getLastName(),
                    e.getEmail()
            );
        }
        if (p instanceof Client c) {
            return new PersonResponse(
                    c.getId(),
                    "CLIENT",
                    c.getClientCode(),
                    c.getFirstName(),
                    c.getLastName(),
                    c.getEmail()
            );
        }
        return new PersonResponse(
                p.getId(),
                "PERSON",
                null,
                p.getFirstName(),
                p.getLastName(),
                p.getEmail()
        );
    }
}