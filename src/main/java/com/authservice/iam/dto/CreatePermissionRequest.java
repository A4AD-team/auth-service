package com.authservice.iam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePermissionRequest(
    @NotBlank @Size(max = 120) String name,
    @Size(max = 255) String description
) {
}
