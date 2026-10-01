package com.jeremy.warehouse.models.DTO;


import com.jeremy.warehouse.models.User.Role;

public record UserRoleUpdateRequest(
        Role role
) { }
