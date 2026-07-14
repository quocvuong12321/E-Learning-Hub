package com.king.lms.e_learning_hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.king.lms.e_learning_hub.dto.quiz_result.QuizResultRequest;
import com.king.lms.e_learning_hub.dto.quiz_result.QuizResultResponse;
import com.king.lms.e_learning_hub.entity.QuizResult;

@Mapper(componentModel = "spring")
public interface QuizResultMapper {

    @Mapping(source = "quiz.id", target = "quizId")
    @Mapping(source = "user.id", target = "userId")
    QuizResultResponse toResponse(QuizResult quizResult);

    @Mapping(target = "quiz", ignore = true) // Sẽ được gán ở Service
    @Mapping(target = "user", ignore = true) // Sẽ được gán ở Service
    @Mapping(target = "startedAt", ignore = true) // Set ở Service với LocalDateTime.now()
    @Mapping(target = "score", ignore = true) // Tính toán ở Service
    @Mapping(target = "isPass", ignore = true) // Tính toán ở Service
    QuizResult toEntity(QuizResultRequest request);
}