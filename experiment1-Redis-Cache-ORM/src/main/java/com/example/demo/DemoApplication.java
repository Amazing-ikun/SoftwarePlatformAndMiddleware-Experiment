package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Spring Boot application bootstrap
 *
 * <p>使用 {@code @MapperScan} 指定 MyBatis mapper 的扫描包，方便自动注入 Spring Bean，
 * 同时不需要显式 register Mapper 接口到 Spring 容器。
 */
@SpringBootApplication
@MapperScan("com.example.demo.repository")
@EnableCaching
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
