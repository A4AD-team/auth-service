package com.authservice.iam.service;

import com.authservice.iam.dto.AuthTokensResponse;
import com.authservice.iam.dto.SignInRequest;
import com.authservice.iam.dto.SignUpRequest;
import com.authservice.iam.entity.RefreshToken;
import com.authservice.iam.entity.Role;
import com.authservice.iam.entity.User;
import com.authservice.iam.repository.RefreshTokenRepository;
import com.authservice.iam.repository.RoleRepository;
import com.authservice.iam.repository.UserRepository;
import com.authservice.iam.util.TokenHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Locale;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public User register(SignUpRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(CONFLICT, "Email already registered");
        }

        Role defaultRole = roleRepository.findByName("USER")
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Default role USER not found"));

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.getRoles().add(defaultRole);
        User saved = userRepository.save(user);
        return userRepository.findById(saved.getId()).orElse(saved);
    }

    @Transactional
    public AuthTokensResponse signIn(SignInRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid credentials");
        }

        JwtService.IssuedToken access = jwtService.issueAccessToken(user);
        JwtService.IssuedToken refresh = jwtService.issueRefreshToken(user);

        RefreshToken entity = new RefreshToken();
        entity.setUser(user);
        entity.setTokenId(refresh.tokenId());
        entity.setTokenHash(TokenHasher.sha256(refresh.token()));
        entity.setExpiresAt(refresh.expiresAt());
        refreshTokenRepository.save(entity);

        return new AuthTokensResponse(
            access.token(),
            refresh.token(),
            "Bearer",
            access.expiresAt(),
            refresh.expiresAt()
        );
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
