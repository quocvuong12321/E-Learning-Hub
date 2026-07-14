package com.king.lms.e_learning_hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.king.lms.e_learning_hub.dto.user_course.UserCourseRequest;
import com.king.lms.e_learning_hub.dto.user_course.UserCourseResponse;
import com.king.lms.e_learning_hub.entity.UserCourse;

@Mapper(componentModel = "spring")
public interface UserCourseMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "enrollmentType", target = "enrollmentType")
    @Mapping(source = "status", target = "status")
    UserCourseResponse toResponse(UserCourse userCourse);

  
    UserCourse toEntity(UserCourseRequest request);
}