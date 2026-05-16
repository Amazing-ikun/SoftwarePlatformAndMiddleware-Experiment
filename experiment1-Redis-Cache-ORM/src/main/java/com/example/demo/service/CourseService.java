package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.repository.CourseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseMapper mapper;
    private final StringRedisTemplate redisTemplate;

    private static final String CACHE_KEY_PREFIX = "course:";
    private static final long CACHE_TTL_MINUTES = 10;

    public Optional<Course> findById(String id) {
        String cacheKey = CACHE_KEY_PREFIX + id;
        String cachedValue = redisTemplate.opsForValue().get(cacheKey);

        if (cachedValue != null) {
            log.info("课程缓存命中 id={}", id);
            return Optional.ofNullable(jsonToCourse(cachedValue));
        }

        log.info("课程缓存未命中，查数据库 id={}", id);
        Course course = mapper.findById(id);

        if (course != null) {
            redisTemplate.opsForValue().set(
                    cacheKey, courseToJson(course), Duration.ofMinutes(CACHE_TTL_MINUTES)
            );
        }
        return Optional.ofNullable(course);
    }

    public List<Course> findByStudentId(String studentId) {
        return mapper.findByStudentId(studentId);
    }

    public List<Course> findAll() {
        return mapper.findAll();
    }

    @Transactional
    public Course create(Course course) {
        mapper.insert(course);
        return course;
    }

    @Transactional
    public Course update(Course course) {
        mapper.update(course);
        redisTemplate.delete(CACHE_KEY_PREFIX + course.getId());
        return course;
    }

    @Transactional
    public void delete(String id) {
        mapper.delete(id);
        redisTemplate.delete(CACHE_KEY_PREFIX + id);
    }

    // JSON 辅助方法
    private String courseToJson(Course course) {
        return String.format("{\"id\":\"%s\",\"name\":\"%s\",\"credits\":%d,\"description\":\"%s\",\"studentId\":\"%s\"}",
                course.getId(), course.getName(), course.getCredits(),
                course.getDescription(), course.getStudentId());
    }

    private Course jsonToCourse(String json) {
        try {
            String id = extractJsonValue(json, "id");
            String name = extractJsonValue(json, "name");
            Integer credits = Integer.valueOf(extractJsonValue(json, "credits"));
            String description = extractJsonValue(json, "description");

            String studentIdStr = extractJsonValue(json, "studentId");
            Long studentId = null;
            if (studentIdStr != null && !studentIdStr.isEmpty()) {
                studentId = Long.valueOf(studentIdStr);
            }

            return new Course(id, name, credits, description, studentId);
        } catch (Exception e) {
            log.error("JSON 解析失败: {}", json, e);
            return null;
        }
    }

    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1 && !key.equals("credits")) return null;
        if (key.equals("credits")) {
            pattern = "\"" + key + "\":";
            start = json.indexOf(pattern);
            if (start == -1) return null;
            start += pattern.length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return json.substring(start, end);
        }
        start += pattern.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}