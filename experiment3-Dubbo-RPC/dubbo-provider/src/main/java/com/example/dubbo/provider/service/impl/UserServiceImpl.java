package com.example.dubbo.provider.service.impl;

import com.example.dubbo.api.entity.User;
import com.example.dubbo.api.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

@DubboService  // 暴露 Dubbo 服务
@Component      // 注册为 Spring Bean
public class UserServiceImpl implements UserService {

    @Override
    public User getUserById(Integer id) {
        System.out.println("Provider 收到请求，查询用户 id: " + id);
        // 模拟数据库查询，返回假数据
        return new User(id, "用户" + id, 20 + id % 30);
    }
}