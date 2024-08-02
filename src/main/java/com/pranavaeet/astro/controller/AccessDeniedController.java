package com.pranavaeet.astro.controller;

import org.apache.http.annotation.Contract;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
