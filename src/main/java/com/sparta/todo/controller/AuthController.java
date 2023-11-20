package com.sparta.todo.controller;

import com.sparta.todo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PatchMapping("/reissue")
    public ResponseEntity<Object> reissue(HttpServletRequest request, HttpServletResponse response) {
        authService.reissue(request, response);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/logout")
    public void logout(HttpServletRequest request) {
        authService.logout(request);
    }
}
