package com.genie.quiz.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation; // <-- Add this import
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // Overloaded method to handle uploads with specific transformations
    public String uploadFile(MultipartFile file, Transformation transformation) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "public_id", UUID.randomUUID().toString(),
                    "transformation", transformation
            ));
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Could not upload file to Cloudinary", e);
        }
    }

    // The original method for user profile pictures (it creates its own transformation)
    public String uploadFile(MultipartFile file) {
        Transformation transformation = new Transformation<>()
                .width(400).height(400).crop("fill")
                .quality("auto").fetchFormat("auto");
        return this.uploadFile(file, transformation);
    }
}