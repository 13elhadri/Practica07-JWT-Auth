package org.example.practica1.auth.services.users;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface AuthUsersService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username);
}