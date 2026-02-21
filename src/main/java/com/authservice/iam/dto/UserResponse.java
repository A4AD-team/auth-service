package com.authservice.iam.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String fullName,
    List<String> roles,
    List<String> permissions,
    JsonNode customClaims
) {
}
