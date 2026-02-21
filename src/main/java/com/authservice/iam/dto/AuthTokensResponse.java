package com.authservice.iam.dto;

import java.time.Instant;

public record AuthTokensResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Instant accessExpiresAt,
    Instant refreshExpiresAt
) {
}
