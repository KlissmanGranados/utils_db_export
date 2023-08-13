package com.kg.dao;

import com.kg.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserDao extends CrudRepository<User, Long> {

    Boolean existsByUser(String user);
    Optional<User> findByUserAndPassword(String user, String password);
}