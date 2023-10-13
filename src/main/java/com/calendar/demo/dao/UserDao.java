package com.calendar.demo.dao;

import java.util.Optional;

import com.calendar.demo.entities.User;

public interface UserDao {
    Optional<User> getUser(final Long id);

    int createUser(User user);
}
