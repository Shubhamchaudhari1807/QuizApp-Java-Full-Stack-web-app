package com.genie.quiz.controller;

import com.genie.quiz.dto.TestDto;
import com.genie.quiz.entity.Test;
import com.genie.quiz.repo.TestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tests") // This is the base URL for public quiz access
@CrossOrigin(origins = "http://127.0.0.1:5501")
public class TestController {

    private final TestRepository testRepository;

    public TestController(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    // This endpoint provides the list of all quizzes for the homepage
    @GetMapping
    public List<TestDto> getAllTests() {
        return testRepository.findAll().stream()
                .map(this::convertTestToDto)
                .collect(Collectors.toList());
    }

    // This endpoint provides the details for a single quiz (e.g., its name)
    @GetMapping("/{id}")
    public ResponseEntity<TestDto> getTestById(@PathVariable Long id) {
        return testRepository.findById(id)
                .map(this::convertTestToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Helper method to convert Entity to DTO
    private TestDto convertTestToDto(Test test) {
        TestDto dto = new TestDto();
        dto.setId(test.getId());
        dto.setName(test.getName());
        dto.setImageUrl(test.getImageUrl());
        return dto;
    }
}