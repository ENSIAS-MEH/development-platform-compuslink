package com.CompusLink.CompusLink.domain.user.service;

import com.CompusLink.CompusLink.domain.user.model.UserPrincipal;
import com.CompusLink.CompusLink.domain.user.model.Users;
import com.CompusLink.CompusLink.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = repo.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("No user found with email: " + username);
        }
        return new UserPrincipal(user);
    }
}
