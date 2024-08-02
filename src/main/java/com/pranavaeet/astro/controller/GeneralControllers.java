package com.pranavaeet.astro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class GeneralControllers {

    
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/")
    public String home() {
        return "welcome";
    }
    @GetMapping("/privacypolicy")
	public String privacypolicy() {
		return "privacypolicy";
	}
    @GetMapping("/support")
	public String contactus() {
		return "support";
	}
   
}
