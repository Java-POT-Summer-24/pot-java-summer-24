package com.coherentsolutions.pot.insurance;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginControl {
    @GetMapping("/")
    public String home(){
        return "Hello world";
    }

    @GetMapping("/login")
    public String login(){
        return "Access to service";
    }
}

