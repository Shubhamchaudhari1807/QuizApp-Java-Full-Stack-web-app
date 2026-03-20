package com.genie.quiz.controller;

import com.genie.quiz.dto.ResultDto;
import com.genie.quiz.dto.TopPerformerDto;
import com.genie.quiz.entity.QuizResult;
import com.genie.quiz.entity.Test;
import com.genie.quiz.entity.User;
import com.genie.quiz.repo.QuizResultRepository;
import com.genie.quiz.repo.TestRepository;
import com.genie.quiz.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "http://127.0.0.1:5501")
public class ResultController {

    private final QuizResultRepository resultRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;

    public ResultController(QuizResultRepository resultRepository, UserRepository userRepository, TestRepository testRepository) {
        this.resultRepository = resultRepository;
        this.userRepository = userRepository;
        this.testRepository = testRepository;
    }

    @PostMapping
    public ResponseEntity<?> saveResult(@RequestBody ResultDto resultDto, Authentication authentication) {
        // Get the logged-in user from the Authentication principal
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        Test test = testRepository.findById(resultDto.getTestId()).orElseThrow();

        QuizResult result = new QuizResult();
        result.setUser(user);
        result.setTest(test);
        result.setScore(resultDto.getScore());
        result.setTotalQuestions(resultDto.getTotalQuestions());
        result.setTimestamp(LocalDateTime.now());

        resultRepository.save(result);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/top-performers")
    public ResponseEntity<List<TopPerformerDto>> getTopPerformers() {
        List<QuizResult> topResults = resultRepository.findTop3ByOrderByScoreDesc();
        List<TopPerformerDto> topPerformers = topResults.stream()
                .map(result -> new TopPerformerDto(
                        result.getUser().getUsername(),
                        result.getUser().getProfileImageUrl(),
                        result.getScore(),
                        result.getTest().getName()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(topPerformers);
    }
}