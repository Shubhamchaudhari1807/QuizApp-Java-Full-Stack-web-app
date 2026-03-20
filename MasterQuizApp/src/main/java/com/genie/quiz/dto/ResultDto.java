package com.genie.quiz.dto;

public class ResultDto {
    private Long testId;
    private int score;
    private int totalQuestions;

    // Getters and Setters
    public Long getTestId() { return testId; }
    public void setTestId(Long testId) { this.testId = testId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
}