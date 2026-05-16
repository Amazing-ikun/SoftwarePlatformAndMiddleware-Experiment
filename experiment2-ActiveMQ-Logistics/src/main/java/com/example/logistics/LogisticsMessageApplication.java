package com.example.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class LogisticsMessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogisticsMessageApplication.class, args);
        System.out.println("物流消息服务已启动...");
    }
}