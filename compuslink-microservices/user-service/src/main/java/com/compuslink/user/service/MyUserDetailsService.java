package com.compuslink.user.service;

import com.compuslink.user.model.UserPrincipal;
import com.compuslink.user.model.Users;
import com.compuslink.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    public MyUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = repo.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with email: " + username);
        }
        return new UserPrincipal(user);
    }
}
