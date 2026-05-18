package com.king.lms.e_learning_hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.king.lms.e_learning_hub.dto.lesson.LessonRequest;
import com.king.lms.e_learning_hub.dto.lesson.LessonResponse;
import com.king.lms.e_learning_hub.entity.Lesson;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    LessonResponse toResponse(Lesson lesson);

    @Mapping(target = "id", ignore = true)
    Lesson toEntity(LessonRequest request);
}