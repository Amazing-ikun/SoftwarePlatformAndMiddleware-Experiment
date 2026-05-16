package com.example.demo.service;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentMapper;
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
public class StudentService {

    private final StudentMapper mapper;
    private final StringRedisTemplate redisTemplate;

    // Redis key 前缀
    private static final String CACHE_KEY_PREFIX = "student:";
    // 过期时间：10分钟
    private static final long CACHE_TTL_MINUTES = 10;
    // 用于解决缓存穿透的空值占位符
    private static final String NULL_PLACEHOLDER = "nil";
    // 空值过期时间：2分钟
    private static final long NULL_TTL_MINUTES = 2;

    /**
     * 查询学生（Cache Aside Pattern）
     * 1. 先从 Redis 查询
     * 2. 命中则直接返回
     * 3. 未命中则查询 MySQL
     * 4. 将结果写入 Redis（设置过期时间）
     */
    public Optional<Student> findById(Long id) {
        String cacheKey = CACHE_KEY_PREFIX + id;

        // 1. 先从 Redis 查询
        String cachedValue = redisTemplate.opsForValue().get(cacheKey);

        if (cachedValue != null) {
            // 命中缓存
            if (NULL_PLACEHOLDER.equals(cachedValue)) {
                // 命中的是空值占位符，表示数据库中没有该记录
                log.info("缓存命中空值，id={} 不存在", id);
                return Optional.empty();
            }
            // 正常数据：将 JSON 字符串反序列化为 Student 对象
            log.info("缓存命中，id={}", id);
            Student student = jsonToStudent(cachedValue);
            return Optional.ofNullable(student);
        }

        // 2. 缓存未命中，查询 MySQL
        log.info("缓存未命中，查询数据库 id={}", id);
        Student student = mapper.findById(id);

        // 3. 将结果写入 Redis
        if (student == null) {
            // 缓存穿透解决：写入空值占位符，短时间过期
            log.info("数据库无数据，写入空值占位符 id={}", id);
            redisTemplate.opsForValue().set(
                    cacheKey,
                    NULL_PLACEHOLDER,
                    Duration.ofMinutes(NULL_TTL_MINUTES)
            );
            return Optional.empty();
        } else {
            // 正常数据：序列化为 JSON 存储，10分钟过期
            redisTemplate.opsForValue().set(
                    cacheKey,
                    studentToJson(student),
                    Duration.ofMinutes(CACHE_TTL_MINUTES)
            );
            return Optional.of(student);
        }
    }

    /**
     * 查询所有（不需要缓存）
     */
    public List<Student> findAll() {
        return mapper.findAll();
    }

    /**
     * 新增学生
     * 新增时不影响已有缓存，只需返回结果
     */
    @Transactional
    public Student create(Student student) {
        mapper.insert(student);
        log.info("新增学生 id={}", student.getId());
        return student;
    }

    /**
     * 更新学生（Cache Aside Pattern 更新策略）
     * 1. 先更新 MySQL
     * 2. 再删除 Redis 中的缓存
     */
    @Transactional
    public Student update(Student student) {
        // 1. 先更新数据库
        mapper.update(student);

        // 2. 删除 Redis 缓存（避免脏数据）
        String cacheKey = CACHE_KEY_PREFIX + student.getId();
        redisTemplate.delete(cacheKey);
        log.info("更新学生 id={}，已删除缓存", student.getId());

        return student;
    }

    /**
     * 删除学生
     * 1. 先删除 MySQL 数据
     * 2. 再删除 Redis 中的缓存
     */
    @Transactional
    public void delete(Long id) {
        // 1. 先删除数据库记录
        mapper.delete(id);

        // 2. 删除 Redis 缓存
        String cacheKey = CACHE_KEY_PREFIX + id;
        redisTemplate.delete(cacheKey);
        log.info("删除学生 id={}，已删除缓存", id);
    }

    // ========== JSON 序列化辅助方法 ==========

    private String studentToJson(Student student) {
        // 简单拼接 JSON（生产环境建议使用 Jackson）
        return String.format("{\"id\":\"%s\",\"name\":\"%s\",\"gender\":\"%s\",\"phone\":\"%s\",\"major\":\"%s\"}",
                student.getId(), student.getName(), student.getGender(),
                student.getPhone(), student.getMajor());
    }

    private Student jsonToStudent(String json) {
        try {
            String idStr = extractJsonValue(json, "id");
            Long id = null;
            if (idStr != null && !idStr.isEmpty()) {
                id = Long.valueOf(idStr);
            }
            String name = extractJsonValue(json, "name");
            String gender = extractJsonValue(json, "gender");
            String phone = extractJsonValue(json, "phone");
            String major = extractJsonValue(json, "major");
            // 使用 @AllArgsConstructor 生成的构造函数
            return new Student(id, name, gender, phone, major, null); // 最后一个参数是 courses，传 null
        } catch (Exception e) {
            log.error("JSON 解析失败: {}", json, e);
            return null;
        }
    }

    private String extractJsonValue(String json, String key) {
        // 尝试带引号的格式: "key":"value"
        String pattern1 = "\"" + key + "\":\"";
        int start = json.indexOf(pattern1);
        if (start != -1) {
            start += pattern1.length();
            int end = json.indexOf("\"", start);
            if (end != -1) {
                return json.substring(start, end);
            }
        }

        // 尝试不带引号的格式: "key":value 或 "key":123（用于数字类型的 id）
        String pattern2 = "\"" + key + "\":";
        start = json.indexOf(pattern2);
        if (start != -1) {
            start += pattern2.length();
            int end = json.indexOf(",", start);
            if (end == -1) {
                end = json.indexOf("}", start);
            }
            if (end != -1) {
                String value = json.substring(start, end).trim();
                // 如果值带引号，去掉引号
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                return value;
            }
        }

        return null;
    }
}
