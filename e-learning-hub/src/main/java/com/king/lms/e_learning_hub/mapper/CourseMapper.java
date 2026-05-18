package com.king.lms.e_learning_hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.king.lms.e_learning_hub.dto.course.CourseRequest;
import com.king.lms.e_learning_hub.dto.course.CourseResponse;
import com.king.lms.e_learning_hub.entity.Course;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    Course toCourse(CourseRequest request);

    CourseResponse toResponse(Course course);


    void updateCourse(CourseRequest request, @MappingTarget Course course);
}