package com.genie.quiz.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    // REMOVED: The @OnDelete annotation is no longer needed here
    private Test test;

    private int score;
    private int totalQuestions;
    private LocalDateTime timestamp;

    // Getters and Setters (unchanged)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Test getTest() { return test; }
    public void setTest(Test test) { this.test = test; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}