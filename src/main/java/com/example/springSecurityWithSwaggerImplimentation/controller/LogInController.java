package com.example.springSecurityWithSwaggerImplimentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LogInController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Thymeleaf login page
    }


//    @GetMapping("/profile")
//    public String profile(Model model, @AuthenticationPrincipal OidcUser user) {
//        model.addAttribute("user", user);
//        return "profile";
//    }

    @GetMapping("profile")
    public String profilePage(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            model.addAttribute("name", principal.getAttribute("name"));
            model.addAttribute("email", principal.getAttribute("email"));
            model.addAttribute("picture", principal.getAttribute("picture"));
        }
        return "profile"; // Thymeleaf template
    }
}
