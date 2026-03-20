package com.genie.quiz.repo;

import com.genie.quiz.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    // We don't need any custom methods here for now, JpaRepository gives us everything we need.
}