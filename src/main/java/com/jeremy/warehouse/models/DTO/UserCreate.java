package com.jeremy.warehouse.models.DTO;

import org.springframework.context.annotation.Role;

public record UserCreate(
        String username,
        String password,
        Role role
) {
}
