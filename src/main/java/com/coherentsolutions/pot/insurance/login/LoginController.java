package com.coherentsolutions.pot.insurance.login;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @GetMapping("/")
    public String home(){
        return "Hello world";
    }

    @GetMapping("/login")
    public String login(){
        return "Access to service";
    }
}

