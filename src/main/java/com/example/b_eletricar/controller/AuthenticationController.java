package com.example.b_eletricar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.b_eletricar.services.AuthenticationService;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired 
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("authenticate")
    public String authenticate() {
        return authenticationService.authenticate();
    }

}
