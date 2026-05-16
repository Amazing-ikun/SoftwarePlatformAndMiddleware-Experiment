package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/{id}")
    public ResponseEntity<Course> get(@PathVariable String id) {
        return courseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public List<Course> getByStudent(@PathVariable String studentId) {
        return courseService.findByStudentId(studentId);
    }

    @GetMapping
    public List<Course> listAll() {
        return courseService.findAll();
    }

    @PostMapping
    public ResponseEntity<Course> create(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.create(course));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> update(@PathVariable String id, @RequestBody Course course) {
        course.setId(id);
        return ResponseEntity.ok(courseService.update(course));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}