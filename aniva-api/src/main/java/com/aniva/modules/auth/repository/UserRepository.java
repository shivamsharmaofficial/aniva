package com.aniva.modules.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aniva.modules.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUuid(UUID uuid);
    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.roles
        WHERE u.email = :email
    """)
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.roles
        WHERE u.phoneNumber = :phone
    """)
    Optional<User> findByPhoneWithRoles(@Param("phone") String phone);
}