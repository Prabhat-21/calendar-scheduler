package com.calendar.demo.controller;

import com.calendar.demo.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.calendar.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public int createUser(User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public Object getUser(@PathVariable("id") final Long id) {
        return userService.getUser(id);
    }
}
