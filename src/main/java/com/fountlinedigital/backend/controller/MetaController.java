package com.fountlinedigital.backend.controller;

import com.fountlinedigital.backend.entity.Department;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/meta")
public class MetaController {

    @GetMapping("/departments")
    public List<String> departments() {
        return Arrays.stream(Department.values())
                .map(Enum::name)
                .toList();
    }
}