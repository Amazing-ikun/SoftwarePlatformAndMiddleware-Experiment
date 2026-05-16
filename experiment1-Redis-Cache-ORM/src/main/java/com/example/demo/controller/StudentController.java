package com.example.demo.controller;

import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    @GetMapping("/{id}")
    public ResponseEntity<Student> get(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 模拟缓存穿透测试
     * GET /students/penetration-test/-1
     * 连续请求不存在的ID，验证第二次请求是否命中空值缓存
     */
    @GetMapping("/penetration-test/{id}")
    public ResponseEntity<String> testPenetration(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        Optional<Student> result = service.findById(id);
        long cost = System.currentTimeMillis() - startTime;

        if (result.isPresent()) {
            return ResponseEntity.ok("查询成功，耗时 " + cost + "ms，数据：" + result.get());
        } else {
            return ResponseEntity.ok("查询无结果，耗时 " + cost + "ms（第二次应该很快）");
        }
    }

    @GetMapping
    public List<Student> listAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student) {
        Student created = service.create(student);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> update(
            @PathVariable Long id, @RequestBody Student student) {
        student.setId(id);
        return ResponseEntity.ok(service.update(student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
