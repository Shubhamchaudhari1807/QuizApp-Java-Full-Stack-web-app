package com.genie.quiz.controller;

import com.genie.quiz.dto.RegisterRequest;
import com.genie.quiz.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://127.0.0.1:5501")
public class  AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<String> register(@RequestPart("user") RegisterRequest registerRequest,
                                           @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        authService.register(registerRequest, profileImage);
        return ResponseEntity.ok("User registered successfully!");
    }
}