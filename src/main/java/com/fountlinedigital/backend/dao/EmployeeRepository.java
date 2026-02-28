package com.fountlinedigital.backend.dao;

import com.fountlinedigital.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmployeeCodeIgnoreCase(String employeeCode);
}