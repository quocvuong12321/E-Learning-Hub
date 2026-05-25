package com.king.lms.e_learning_hub.mapper;

import org.mapstruct.Mapper;

import com.king.lms.e_learning_hub.dto.lesson.LessonRequest;
import com.king.lms.e_learning_hub.dto.lesson.LessonResponse;
import com.king.lms.e_learning_hub.entity.Lesson;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    LessonResponse toResponse(Lesson lesson);
 
    Lesson toEntity(LessonRequest request);
}