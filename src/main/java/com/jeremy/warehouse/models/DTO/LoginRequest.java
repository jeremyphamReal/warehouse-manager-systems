package com.jeremy.warehouse.models.DTO;

public record LoginRequest(
        String username,
        String password
) {
}
