package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Course entity
 *
 * <p>与 MySQL 表 `course` 对应，字段可根据业务自行增删。
 * 这里展示了一个最常见的四字段示例。
 *
 * <pre>
 *  id      BIGINT AUTO_INCREMENT PRIMARY KEY
 *  name    VARCHAR(255)
 *  credits INT
 *  teacher VARCHAR(255)
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    /**
     * 主键，自增
     */
    private String id;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 学分
     */
    private Integer credits;

    /**
     * 课程介绍
     */
    private String description;

    /**
     * 对应的学生 id
     */
    private Long studentId;
}
