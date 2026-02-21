package com.authservice.iam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateRoleRequest(
    @NotBlank @Size(max = 120) String name,
    @Size(max = 255) String description,
    List<String> permissions
) {
}
