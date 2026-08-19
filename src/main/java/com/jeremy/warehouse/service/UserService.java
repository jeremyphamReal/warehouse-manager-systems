package com.jeremy.warehouse.service;

import com.jeremy.warehouse.models.User;
import com.jeremy.warehouse.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepo repo;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);

    public User save(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public List<User> findAllStaff() {
        List<User> staff= repo.findByRole("STAFF");
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
        if(existingUser.getPassword()!=null && existingUser.getPassword().isEmpty()){
            existingUser.setPassword(user.getPassword());
        }
        existingUser.setRole(user.getRole());
        return repo.save(existingUser);
    }
}
