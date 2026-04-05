package com.aniva.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.aniva.modules.auth.entity.User;
import com.aniva.modules.auth.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // 🔥 USE REDIS CACHE INSTEAD OF DB
        User user = loadUserByEmail(email);

        List<SimpleGrantedAuthority> authorities =
                user.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                        .toList();

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                null, // password not needed because you validate manually
                authorities
        );
    }

        // ===============================
        // 🔥 REDIS CACHE - USER SESSION
        // ===============================
        @Cacheable(value = "user-session", key = "#email", unless = "#result == null")
        public User loadUserByEmail(String email) {

        // 🔥 First time DB hit, next time Redis ⚡
        return userRepository.findByEmailWithRoles(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
        }

}