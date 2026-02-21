package com.authservice.iam.service;

import com.authservice.iam.config.JwtProperties;
import com.authservice.iam.entity.Permission;
import com.authservice.iam.entity.Role;
import com.authservice.iam.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private final JwtEncoder accessJwtEncoder;
    private final JwtEncoder refreshJwtEncoder;
    private final JwtProperties properties;

    public JwtService(@Qualifier("accessJwtEncoder") JwtEncoder accessJwtEncoder,
                      @Qualifier("refreshJwtEncoder") JwtEncoder refreshJwtEncoder,
                      JwtProperties properties) {
        this.accessJwtEncoder = accessJwtEncoder;
        this.refreshJwtEncoder = refreshJwtEncoder;
        this.properties = properties;
    }

    public IssuedToken issueAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.getAccessTtlMinutes(), ChronoUnit.MINUTES);

        List<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .sorted()
            .toList();

        Set<String> permissions = user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getName)
            .collect(Collectors.toSet());

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
            .issuer(properties.getIssuer())
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("roles", roles)
            .claim("permissions", permissions);

        if (user.getDepartment() != null) {
            claims.claim("department_id", user.getDepartment().getId().toString());
            claims.claim("department_name", user.getDepartment().getName());
        }

        if (user.getCustomClaims() != null) {
            claims.claim("custom", user.getCustomClaims());
        }

        String token = accessJwtEncoder.encode(
            JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims.build())
        ).getTokenValue();
        return new IssuedToken(token, expiresAt, null);
    }

    public IssuedToken issueRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.getRefreshTtlMinutes(), ChronoUnit.MINUTES);
        String tokenId = UUID.randomUUID().toString();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(properties.getIssuer())
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(user.getId().toString())
            .id(tokenId)
            .claim("email", user.getEmail())
            .build();

        String token = refreshJwtEncoder.encode(
            JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)
        ).getTokenValue();
        return new IssuedToken(token, expiresAt, tokenId);
    }

    public record IssuedToken(String token, Instant expiresAt, String tokenId) {
    }
}
