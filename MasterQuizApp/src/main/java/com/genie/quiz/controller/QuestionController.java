package com.genie.quiz.controller;

import com.genie.quiz.dto.OptionDto;
import com.genie.quiz.dto.QuestionDto;
import com.genie.quiz.entity.Option;
import com.genie.quiz.entity.Question;
import com.genie.quiz.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5501")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/api/tests/{testId}/questions")
    public List<QuestionDto> getQuestionsForTest(@PathVariable Long testId) {
        List<Question> questions = questionService.getQuestionsForTest(testId);
        return questions.stream()
                .map(this::convertQuestionToDto)
                .collect(Collectors.toList());
    }

    // --- Helper Methods ---
    private QuestionDto convertQuestionToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuestionText(question.getQuestionText());
        dto.setOptions(question.getOptions().stream()
                .map(this::convertOptionToDto)
                .collect(Collectors.toList()));
        return dto;
    }

    private OptionDto convertOptionToDto(Option option) {
        OptionDto dto = new OptionDto();
        dto.setId(option.getId());
        dto.setOptionText(option.getOptionText());
        dto.setCorrect(option.isCorrect());
        return dto;
    }
}