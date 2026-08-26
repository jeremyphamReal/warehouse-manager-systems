package com.jeremy.warehouse.controller;

import com.jeremy.warehouse.models.User;
import com.jeremy.warehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/users/admin")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public User register(@RequestBody User user){
        return userService.save(user);
    }

    //TODO: Lay Danh sach Staff
    @GetMapping("/list_all_staff")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> findAll(){
        return userService.findAllStaff();
    }
    //TODO: Xoa Staff dua vao id
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok("Đã xóa user thành công!");
    }

    //TODO: Update password, role cho 1 staff
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) throws Exception {
        User updatedUser = userService.update(id, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}
