package com.authservice.iam.dto;

import java.util.UUID;

public record DepartmentResponse(
    UUID id,
    String name
) {
}
