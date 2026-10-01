package com.jeremy.warehouse.repository;

import com.jeremy.warehouse.models.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);

    List<User> findByRole(String role);
}
