package com.genie.quiz.dto;

public class TopPerformerDto {
    private String username;
    private String profileImageUrl;
    private int score;
    private String testName;

    // Constructor, Getters, and Setters
    public TopPerformerDto(String username, String profileImageUrl, int score, String testName) {
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.score = score;
        this.testName = testName;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
}