package com.lifegpa.demo.controllers;

import com.lifegpa.demo.payload.LoginRequest;
import com.lifegpa.demo.payload.SignUpRequest;
import com.lifegpa.demo.services.AuthService;
import com.lifegpa.demo.services.MapValidationErrorService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final MapValidationErrorService errorService;

    public AuthController(AuthService authService, MapValidationErrorService errorService) {
        this.authService = authService;
        this.errorService = errorService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {

        System.out.println("sign in");
        ResponseEntity<?> errors = errorService.mapValidationService(result);
        if (errors != null) return errors;

        return ResponseEntity.ok(authService.loginRequest(loginRequest));


    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, BindingResult result) {

        System.out.println("sign up");
        ResponseEntity<?> error = errorService.mapValidationService(result);
        if (error != null) return error;

        return authService.registerUser(signUpRequest);
    }
}
