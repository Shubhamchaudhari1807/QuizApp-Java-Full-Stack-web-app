package com.genie.quiz.service;

import com.genie.quiz.dto.QuestionDto;
import com.genie.quiz.entity.Option;
import com.genie.quiz.entity.Question;
import com.genie.quiz.entity.Test;
import com.genie.quiz.repo.QuestionRepository;
import com.genie.quiz.repo.TestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;

    /**
     * We are using Constructor Injection here instead of @Autowired.
     * This is a best practice in Spring for required dependencies.
     */
    public QuestionService(QuestionRepository questionRepository, TestRepository testRepository) {
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
    }

    /**
     * Fetches all questions that belong to a specific test.
     * This replaces the old getAllQuestions() method.
     * @param testId The ID of the test whose questions are to be fetched.
     * @return A list of Question entities.
     */
    public List<Question> getQuestionsForTest(Long testId) {
        return questionRepository.findByTestId(testId);
    }

    /**
     * Saves a new question and associates it with a specific test.
     * @param testId The ID of the test this question belongs to.
     * @param question The Question object to be saved.
     * @return The saved Question entity with its generated ID.
     */
    @Transactional
    public Question createQuestion(Long testId, Question question) {
        // Find the test by its ID. If not found, throw an exception.
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test not found with id: " + testId));

        // Associate the question with the found test.
        question.setTest(test);

        // This is a crucial step for JPA bidirectional relationships!
        // We must manually set the question on each option to establish the link.
        question.getOptions().forEach(option -> option.setQuestion(question));

        // Save the question. Thanks to CascadeType.ALL, the options will be saved too.
        return questionRepository.save(question);
    }

    // Add this new method inside your QuestionService.java class

    @Transactional
    public Question updateQuestion(Long questionId, QuestionDto questionDetails) {
        // 1. Find the existing question in the database
        Question existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + questionId));

        // 2. Update the question text
        existingQuestion.setQuestionText(questionDetails.getQuestionText());

        // 3. Clear the old options
        existingQuestion.getOptions().clear();

        // 4. Create and add the new options from the DTO
        questionDetails.getOptions().forEach(optionDto -> {
            Option newOption = new Option();
            newOption.setOptionText(optionDto.getOptionText());
            newOption.setCorrect(optionDto.isCorrect());
            newOption.setQuestion(existingQuestion); // Link new option back to the question
            existingQuestion.getOptions().add(newOption);
        });

        // 5. Save the updated question. JPA will handle deleting old options and adding new ones.
        return questionRepository.save(existingQuestion);
    }
}