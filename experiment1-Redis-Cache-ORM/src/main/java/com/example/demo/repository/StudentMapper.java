package com.example.demo.repository;

import com.example.demo.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper {

    @Select("SELECT id, name, gender, phone, major FROM student WHERE id = #{id}")
    Student findById(Long id);

    @Select("SELECT s.*, c.id as c_id, c.name as c_name, c.credits, c.description, c.student_id " +
            "FROM student s LEFT JOIN course c ON s.id = c.student_id WHERE s.id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "major", column = "major"),
            @Result(property = "courses", column = "id", javaType = List.class,
                    many = @Many(select = "com.example.demo.repository.CourseMapper.findByStudentId"))
    })
    Student findByIdWithCourses(Long id);

    @Select("SELECT id, name, gender, phone, major FROM student")
    List<Student> findAll();

    @Insert("INSERT INTO student(name, gender, phone, major) " +
            "VALUES(#{name}, #{gender}, #{phone}, #{major})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Student student);

    @Update("UPDATE student SET name = #{name}, gender = #{gender}, " +
            "phone = #{phone}, major = #{major} WHERE id = #{id}")
    int update(Student student);

    @Delete("DELETE FROM student WHERE id = #{id}")
    int delete(Long id);
}
