package com.jeremy.warehouse.service;

import com.jeremy.warehouse.models.User;
import com.jeremy.warehouse.models.UserPrincipal;
import com.jeremy.warehouse.repository.UserRepo;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService, UserDetailsPasswordService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails updatePassword(UserDetails user, @Nullable String newPassword) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userFromDb = userRepo.findByUsername(username);
        if(userFromDb==null){
            throw new UsernameNotFoundException("User not found !!!");
        }
        return new UserPrincipal(userFromDb);
    }
}
