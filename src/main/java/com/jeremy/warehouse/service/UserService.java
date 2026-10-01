package com.jeremy.warehouse.service;

import com.jeremy.warehouse.config.SecurityConfig;
import com.jeremy.warehouse.models.DTO.UserCreateRequest;
import com.jeremy.warehouse.models.DTO.UserResponse;
import com.jeremy.warehouse.models.DTO.UserRoleUpdateRequest;
import com.jeremy.warehouse.models.User.Role;
import com.jeremy.warehouse.models.User.User;
import com.jeremy.warehouse.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepo repo;
    @Autowired
    private PasswordEncoder passwordEncoder;
//    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);

    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public List<User> findAllStaff() {
        List<User> staff= repo.findByRole("staff");
        if(staff.isEmpty()){
            throw new RuntimeException("Không tìm thấy danh sách staff trong hệ thống");
        }
        return staff;
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public User update(Long id, User user) {
        User existingUser = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        return repo.save(existingUser);
    }

    public UserResponse createUser(UserCreateRequest request) {
        if (request.username() == null || request.username().isBlank()) {
            throw new IllegalArgumentException("Username can not be empty");
        }
        if (repo.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password())) // luôn hash, không lưu plain text
                .role(request.role() != null ? request.role() : Role.STAFF) // default role
                .build();

        User saved = repo.save(user);
        return UserResponse.from(saved);
    }

    public UserResponse updateUserRole(Long id, UserRoleUpdateRequest request) {
        User user = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (request.role() == null) {
            throw new IllegalArgumentException("Role can not blank");
        } else if(user.getRole() == request.role()){
            return UserResponse.from(user);
        }

        user.setRole(request.role());
        User saved = repo.save(user);
        return UserResponse.from(saved);
    }
}
