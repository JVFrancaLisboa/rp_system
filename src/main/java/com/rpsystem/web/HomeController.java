package com.rpsystem.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "RP System");
        model.addAttribute("message", "Sistema inicial com Spring Boot, Bootstrap e MySQL.");
        return "index";
    }
}
