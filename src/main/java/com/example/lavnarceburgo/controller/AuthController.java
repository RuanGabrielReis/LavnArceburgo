package com.example.lavnarceburgo.controller;

import com.example.lavnarceburgo.dto.auth.LoginRequestDTO;
import com.example.lavnarceburgo.dto.auth.LoginResponseDTO;
import com.example.lavnarceburgo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto
    ) {
        return ResponseEntity.ok(
                authService.login(dto)
        );
    }
}