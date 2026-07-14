package com.king.lms.e_learning_hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.king.lms.e_learning_hub.dto.user_progress.UserProgressRequest;
import com.king.lms.e_learning_hub.dto.user_progress.UserProgressResponse;
import com.king.lms.e_learning_hub.entity.UserProgress;

@Mapper(componentModel = "spring")
public interface UserProgressMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "lesson.id", target = "lessonId")
    UserProgressResponse toResponse(UserProgress userProgress);

    @Mapping(target = "user", ignore = true) // Sẽ được gán ở Service
    @Mapping(target = "lesson", ignore = true) // Sẽ được gán ở Service
    UserProgress toEntity(UserProgressRequest request);
}