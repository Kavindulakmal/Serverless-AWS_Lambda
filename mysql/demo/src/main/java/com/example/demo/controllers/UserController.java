package com.example.demo.controllers;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST, value = "")
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }

    @RequestMapping(method = RequestMethod.GET, value = "")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public User getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user){
        return userService.updateUser(id,user);
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
}
