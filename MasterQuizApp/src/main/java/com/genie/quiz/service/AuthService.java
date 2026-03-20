package com.genie.quiz.service;

import com.genie.quiz.dto.RegisterRequest;
import com.genie.quiz.dto.UserDto;
import com.genie.quiz.entity.User;
import com.genie.quiz.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
    }

    public void register(RegisterRequest registerRequest, MultipartFile file) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = cloudinaryService.uploadFile(file);
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("USER");
        user.setProfileImageUrl(imageUrl);

        userRepository.save(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertUserToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void promoteUserToAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        user.setRole("ADMIN");
        userRepository.save(user);
    }

    // --- ADDED: Method to demote an admin to user with safety checks ---
    @Transactional
    public void demoteAdminToUser(Long userId, String currentAdminUsername) {
        User userToDemote = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Safety Check 1: Prevent the default 'admin' from being demoted.
        if ("admin".equalsIgnoreCase(userToDemote.getUsername())) {
            throw new IllegalArgumentException("fuck bitch !! i am god of this server !!.");
        }

        // Safety Check 2: Prevent an admin from demoting themselves.
        if (userToDemote.getUsername().equalsIgnoreCase(currentAdminUsername)) {
            throw new IllegalArgumentException("You cannot demote yourself.");
        }

        userToDemote.setRole("USER");
        userRepository.save(userToDemote);
    }

    private UserDto convertUserToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        return dto;
    }
}