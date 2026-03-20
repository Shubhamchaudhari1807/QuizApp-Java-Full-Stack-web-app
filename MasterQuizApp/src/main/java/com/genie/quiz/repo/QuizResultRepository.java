package com.genie.quiz.repo;

import com.genie.quiz.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    // This is a special method provided by Spring Data JPA.
    // By naming it this way, Spring will automatically create a query
    // to find the top 3 results with the highest scores.
    List<QuizResult> findTop3ByOrderByScoreDesc();
}