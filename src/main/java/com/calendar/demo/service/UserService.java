package com.calendar.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.calendar.demo.dao.UserDao;
import com.calendar.demo.entities.User;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User getUser(Long id) {
        return userDao.getUser(id).orElse(null);
    }


    public int createUser(User user) {
        return userDao.createUser(user);
    }
}
