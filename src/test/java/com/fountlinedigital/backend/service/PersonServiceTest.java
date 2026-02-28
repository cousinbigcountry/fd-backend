package com.fountlinedigital.backend.service;

import com.fountlinedigital.backend.dao.PersonRepository;
import com.fountlinedigital.backend.dto.ClientCreateRequest;
import com.fountlinedigital.backend.dto.PersonResponse;
import com.fountlinedigital.backend.entity.Client;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PersonServiceTest {

    @Test
    void createClientReturnsPersonResponseWithGeneratedCode() {

        PersonRepository repo = Mockito.mock(PersonRepository.class);
        CodeGeneratorService codeGen = new CodeGeneratorService();

        when(repo.save(Mockito.any(Client.class))).thenAnswer(invocation -> {
            Client saved = invocation.getArgument(0);
            saved.setId(123L);
            return saved;
        });

        PersonService service = new PersonService(repo, codeGen);

        ClientCreateRequest request = new ClientCreateRequest(
                "Alice",
                "Johnson",
                "alice@example.com",
                "Atlas Systems"
        );

        PersonResponse response = service.createClient(request);

        assertNotNull(response);
        assertEquals(123L, response.id());
        assertEquals("CLIENT", response.type());

        assertNotNull(response.code());
        assertTrue(response.code().matches("^FDC\\d{3}$"),
                "Expected client code format FDC### but got: " + response.code());

        assertEquals("Alice", response.firstName());
        assertEquals("Johnson", response.lastName());
        assertEquals("alice@example.com", response.email());
    }
}