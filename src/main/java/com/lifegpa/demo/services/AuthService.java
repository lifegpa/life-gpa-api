package com.lifegpa.demo.services;

import com.lifegpa.demo.exception.ResourceNotFoundException;
import com.lifegpa.demo.models.Role;
import com.lifegpa.demo.models.RoleName;
import com.lifegpa.demo.models.User;
import com.lifegpa.demo.payload.ApiResponse;
import com.lifegpa.demo.payload.JwtAuthenticationResponse;
import com.lifegpa.demo.payload.LoginRequest;
import com.lifegpa.demo.payload.SignUpRequest;
import com.lifegpa.demo.repositories.RoleRepository;
import com.lifegpa.demo.repositories.UserRepository;
import com.lifegpa.demo.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public JwtAuthenticationResponse loginRequest(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return new JwtAuthenticationResponse(jwt);
    }

    public ResponseEntity<?> registerUser(SignUpRequest signUpRequest) {


        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "Email is already taken"), HttpStatus.BAD_REQUEST);

        }

        User user = createUser(signUpRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username")
                .buildAndExpand(user.getEmail()).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true, "User Registered"));
    }


    private User createUser(SignUpRequest signUpRequest) {
        User user = new User(
                signUpRequest.getName(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword());


        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException(""));

        user.setRoles(Collections.singleton(userRole));
        return userRepository.save(user);
    }
}
