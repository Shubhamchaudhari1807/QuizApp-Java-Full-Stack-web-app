package com.genie.quiz.dto;

public class TestDto {
    private Long id;
    private String name;

    // ADDED: Field for the image URL
    private String imageUrl;

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // --- ADDED: Getter and Setter for imageUrl ---
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}