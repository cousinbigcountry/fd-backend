package com.fountlinedigital.backend.dao;

import com.fountlinedigital.backend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByClientCodeIgnoreCase(String clientCode);
}