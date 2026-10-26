package com.project.supportchatbot.repository;

import com.project.supportchatbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);
}
