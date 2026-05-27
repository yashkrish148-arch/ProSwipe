package com.proswipe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin(origins = "*")
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
}
