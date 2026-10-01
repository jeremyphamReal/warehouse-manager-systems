package com.jeremy.warehouse.models.DTO;

import com.jeremy.warehouse.models.User.Role;
import com.jeremy.warehouse.models.User.User;

public record UserResponse(
        Long id,
        String username,
        Role role
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }
}
