package com.genie.quiz.controller;

import com.genie.quiz.dto.OptionDto;
import com.genie.quiz.dto.QuestionDto;
import com.genie.quiz.dto.UserDto; // ADDED
import com.genie.quiz.entity.Option;
import com.genie.quiz.entity.Question;
import com.genie.quiz.entity.Test;
import com.genie.quiz.repo.TestRepository;
import com.genie.quiz.service.AIService;
import com.genie.quiz.service.AuthService; // ADDED
import com.genie.quiz.service.QuestionService;
import com.genie.quiz.service.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // ADDED
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://127.0.0.1:5501")
public class AdminController {

    private final TestService testService;
    private final TestRepository testRepository;
    private final QuestionService questionService;
    private final AIService aiService;
    private final AuthService authService; // ADDED

    // UPDATED Constructor to include AuthService
    public AdminController(TestService testService, TestRepository testRepository, QuestionService questionService, AIService aiService, AuthService authService) {
        this.testService = testService;
        this.testRepository = testRepository;
        this.questionService = questionService;
        this.aiService = aiService;
        this.authService = authService; // ADDED
    }

    // --- Test Management (Unchanged) ---
    @PostMapping(value = "/tests", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Test> createTest(@RequestPart("name") String name,
                                           @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        Test createdTest = testService.createTest(name, imageFile);
        return ResponseEntity.ok(createdTest);
    }

    @DeleteMapping("/tests/{testId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteTest(@PathVariable Long testId) {
        Test testToDelete = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalStateException("Test not found with id: " + testId));
        testService.deleteTest(testToDelete);
        return ResponseEntity.noContent().build();
    }

    // --- Question Management (Unchanged) ---
    @PostMapping("/tests/{testId}/questions")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<QuestionDto> createQuestion(@PathVariable Long testId, @RequestBody QuestionDto questionDto) {
        Question questionToSave = convertDtoToQuestion(questionDto);
        Question savedQuestion = questionService.createQuestion(testId, questionToSave);
        return ResponseEntity.ok(convertQuestionToDto(savedQuestion));
    }

    // --- AI Question Generation Endpoint (Unchanged) ---
    @PostMapping("/questions/generate-ai")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<QuestionDto>> generateAiQuestions(@RequestBody Map<String, Object> payload) {
        try {
            String topic = (String) payload.get("topic");
            Integer count = (Integer) payload.get("count");
            if (topic == null || topic.isBlank() || count == null || count <= 0) {
                return ResponseEntity.badRequest().build();
            }
            List<QuestionDto> generatedQuestions = aiService.generateQuestions(topic, count);
            return ResponseEntity.ok(generatedQuestions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- ADDED: User Management Endpoints ---
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}/promote")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> promoteUserToAdmin(@PathVariable Long userId) {
        authService.promoteUserToAdmin(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}/demote")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> demoteAdminToUser(@PathVariable Long userId, Authentication authentication) {
        try {
            // We pass the current admin's username for the safety check
            String currentAdminUsername = authentication.getName();
            authService.demoteAdminToUser(userId, currentAdminUsername);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            // This catches the specific safety check errors from the service
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // --- Helper Methods (Unchanged) ---
    private QuestionDto convertQuestionToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuestionText(question.getQuestionText());
        if (question.getOptions() != null) {
            dto.setOptions(question.getOptions().stream()
                    .map(this::convertOptionToDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private OptionDto convertOptionToDto(Option option) {
        OptionDto dto = new OptionDto();
        dto.setId(option.getId());
        dto.setOptionText(option.getOptionText());
        dto.setCorrect(option.isCorrect());
        return dto;
    }

    private Question convertDtoToQuestion(QuestionDto questionDto) {
        Question question = new Question();
        question.setQuestionText(questionDto.getQuestionText());
        if (questionDto.getOptions() != null) {
            question.setOptions(questionDto.getOptions().stream()
                    .map(optionDto -> {
                        Option option = new Option();
                        option.setOptionText(optionDto.getOptionText());
                        option.setCorrect(optionDto.isCorrect());
                        option.setQuestion(question);
                        return option;
                    })
                    .collect(Collectors.toList()));
        }
        return question;
    }
}