package com.fountlinedigital.backend.dao;

import com.fountlinedigital.backend.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {

    List<Person> findByLastNameContainingIgnoreCase(String lastName);

    List<Person> findByEmailContainingIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

}