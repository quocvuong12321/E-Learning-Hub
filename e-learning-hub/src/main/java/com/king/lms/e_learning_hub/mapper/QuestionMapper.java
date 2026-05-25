package com.king.lms.e_learning_hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.king.lms.e_learning_hub.dto.question.QuestionRequest;
import com.king.lms.e_learning_hub.dto.question.QuestionResponse;
import com.king.lms.e_learning_hub.entity.Question;

@Mapper(componentModel = "spring", uses = {AnswerMapper.class})
public interface QuestionMapper {

    @Mapping(source = "quiz.id", target = "quizId")
    QuestionResponse toResponse(Question question);

    @Mapping(target = "quiz", ignore = true) // Sẽ được gán ở Service
    @Mapping(source = "answerRequests", target = "answers") // Chỉ định rõ nguồn và đích
    Question toEntity(QuestionRequest request);
}