package com.king.lms.e_learning_hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.king.lms.e_learning_hub.dto.quiz.QuizRequest;
import com.king.lms.e_learning_hub.dto.quiz.QuizResponse;
import com.king.lms.e_learning_hub.entity.Quiz;

@Mapper(componentModel = "spring", uses = {QuestionMapper.class})
public interface QuizMapper {

    @Mapping(source = "lesson.id", target = "lessonId")
    QuizResponse toResponse(Quiz quiz);

    @Mapping(target = "lesson", ignore = true) // Sẽ được gán ở Service
    @Mapping(source = "questionRequests", target = "questions") 
    Quiz toEntity(QuizRequest request);
}