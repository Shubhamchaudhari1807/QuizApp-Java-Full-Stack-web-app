package com.genie.quiz.repo;

import com.genie.quiz.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * This is the key method for your application!
     * Spring Data JPA automatically creates the query for us based on the method name.
     * It will find all Question entities that are linked to a specific test's ID.
     */
    List<Question> findByTestId(Long testId);

}