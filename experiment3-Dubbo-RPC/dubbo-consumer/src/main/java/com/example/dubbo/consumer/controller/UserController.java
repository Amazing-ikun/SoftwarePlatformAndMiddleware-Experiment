package com.example.dubbo.consumer.controller;

import com.example.dubbo.api.entity.User;
import com.example.dubbo.api.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @DubboReference
    private UserService userService;

    @GetMapping("/getUser")
    public User getUser(@RequestParam(name = "id") Integer id) {
        System.out.println("Consumer 收到 HTTP 请求，准备调用 Provider");
        User user = userService.getUserById(id);
        System.out.println("Consumer 收到返回结果: " + user);
        return user;
    }
}