package com.jeremy.warehouse.controller;

import com.jeremy.warehouse.models.User;
import com.jeremy.warehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("register")
    @PreAuthorize("hasRole=('admin)")
    public User register(User user){
        return userService.save(user);
    }
}
