package com.genie.quiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String landingPage() {
        // This tells Spring to serve the "landing.html" file
        // when a user visits the root URL.
        return "landing.html";
    }
}