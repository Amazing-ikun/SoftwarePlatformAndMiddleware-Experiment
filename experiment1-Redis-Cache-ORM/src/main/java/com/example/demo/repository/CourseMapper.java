package com.example.demo.repository;

import com.example.demo.entity.Course;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseMapper {

    @Select("SELECT id, name, credits, description, student_id FROM course WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "credits", column = "credits"),
            @Result(property = "description", column = "description"),
            @Result(property = "studentId", column = "student_id")
    })
    Course findById(String id);

    @Select("SELECT id, name, credits, description, student_id FROM course WHERE student_id = #{studentId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "credits", column = "credits"),
            @Result(property = "description", column = "description"),
            @Result(property = "studentId", column = "student_id")
    })
    List<Course> findByStudentId(String studentId);

    @Select("SELECT id, name, credits, description, student_id FROM course")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "credits", column = "credits"),
            @Result(property = "description", column = "description"),
            @Result(property = "studentId", column = "student_id")
    })
    List<Course> findAll();

    @Insert("INSERT INTO course(id, name, credits, description, student_id) " +
            "VALUES(#{id}, #{name}, #{credits}, #{description}, #{studentId})")
    int insert(Course course);

    @Update("UPDATE course SET name = #{name}, credits = #{credits}, " +
            "description = #{description}, student_id = #{studentId} WHERE id = #{id}")
    int update(Course course);

    @Delete("DELETE FROM course WHERE id = #{id}")
    int delete(String id);
}