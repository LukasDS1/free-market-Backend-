package com.freemarket.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemarket.auth_service.model.User;
import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User,Long>{
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsById (Long userId);

    Optional<User> findByUsername(String username);
}
