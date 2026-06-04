package com.zisakuapp.zisaku_backend.repository;

import com.zisakuapp.zisaku_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(Long userId);
    Optional<User> findByUserEmail(String userEmail);
    boolean existsByUserEmail(String userEmail);
}