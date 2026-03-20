package com.genie.quiz.repo;

import com.genie.quiz.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Security will use this method to find a user by their username during the login process
    Optional<User> findByUsername(String username);
}