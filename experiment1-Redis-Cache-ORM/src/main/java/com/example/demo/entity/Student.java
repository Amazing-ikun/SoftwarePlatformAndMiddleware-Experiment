package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Student 实体
 *
 * <p>对应数据库表 `student`，字段如下
 *   id      BIGINT AUTO_INCREMENT PRIMARY KEY
 *   name    VARCHAR(255)
 *   gender  VARCHAR(10)  // 例如“男/女”或“Male/Female”
 *   phone   VARCHAR(20)
 *   major   VARCHAR(50)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student implements Serializable {
    private Long id;        // 主键
    private String name;      // 姓名
    private String gender;    // 性别
    private String phone;     // 联系电话
    private String major;     // 主修

    // 一对多
    private List<Course> courses;

    public Student(Object o, String name, String gender, String phone, String major) {}
}
