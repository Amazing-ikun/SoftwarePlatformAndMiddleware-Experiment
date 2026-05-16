package com.example.demo;

import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private StudentService studentService;

    @Test
    void contextLoads() {
        // 1. 创建一条 Student（id 由数据库自动生成）
        Student s = new Student(null, "张三", "男", "15811112222", "软件工程", null);
        Student created = studentService.create(s);

        // 2. 检查 id 已被分配
        assertThat(created.getId()).isNotNull();

        // 3. 根据 id 读取，检查数据完整性（注意：id 是 String 类型）
        Student fetched = studentService.findById(Long.parseLong(created.getId()))
                .orElseThrow(() -> new AssertionError("Student not found"));
        assertThat(fetched.getName()).isEqualTo("张三");
        assertThat(fetched.getGender()).isEqualTo("男");
        assertThat(fetched.getPhone()).isEqualTo("15811112222");
        assertThat(fetched.getMajor()).isEqualTo("软件工程");

        // 4. 删除并验证删除成功（delete 方法接收 Long 类型）
        studentService.delete(Long.parseLong(created.getId()));
        assertThat(studentService.findById(Long.parseLong(created.getId())).isPresent())
                .isFalse();
    }
}