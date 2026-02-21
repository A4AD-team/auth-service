package com.authservice.iam.controller;

import com.authservice.iam.dto.AuthTokensResponse;
import com.authservice.iam.dto.SignInRequest;
import com.authservice.iam.dto.SignUpRequest;
import com.authservice.iam.dto.UserResponse;
import com.authservice.iam.entity.User;
import com.authservice.iam.service.AuthService;
import com.authservice.iam.util.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign_up")
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        User user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toUserResponse(user));
    }

    @PostMapping("/sign_in")
    public AuthTokensResponse signIn(@Valid @RequestBody SignInRequest request) {
        return authService.signIn(request);
    }
}
