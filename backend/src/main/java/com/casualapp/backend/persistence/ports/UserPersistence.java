package com.casualapp.backend.persistence.ports;

import java.util.List;
import java.util.Optional;

import com.casualapp.backend.model.User;

public interface UserPersistence {
    Optional<User> findById(Long userId);

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findAll();

    List<User> findByRole(String role);

    List<User> findByStatus(String status);

    User save(User user);

    User update(User user);

    boolean deleteById(Long userId);
}