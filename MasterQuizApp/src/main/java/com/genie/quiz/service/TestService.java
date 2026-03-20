package com.genie.quiz.service;

import com.cloudinary.Transformation;
import com.genie.quiz.entity.Test;
import com.genie.quiz.repo.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Use Spring's Transactional
import org.springframework.web.multipart.MultipartFile;

@Service
public class TestService {

    private final TestRepository testRepository;
    private final CloudinaryService cloudinaryService;

    public TestService(TestRepository testRepository, CloudinaryService cloudinaryService) {
        this.testRepository = testRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public Test createTest(String name, MultipartFile imageFile) {
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = cloudinaryService.uploadFile(imageFile, new Transformation<>()
                    .width(800).height(600).crop("limit")
                    .quality("auto").fetchFormat("auto"));
        }
        Test newTest = new Test();
        newTest.setName(name);
        newTest.setImageUrl(imageUrl);
        return testRepository.save(newTest);
    }

    @Transactional
    public void deleteTest(Test test) {
        testRepository.delete(test);
    }
}