package com.king.lms.e_learning_hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.king.lms.e_learning_hub.dto.answer.AnswerRequest;
import com.king.lms.e_learning_hub.dto.answer.AnswerResponse;
import com.king.lms.e_learning_hub.entity.Answer;

@Mapper(componentModel = "spring")
public interface AnswerMapper {

    @Mapping(source = "question.id", target = "questionId")
    AnswerResponse toResponse(Answer answer);

    @Mapping(target = "question", ignore = true) // Sẽ được gán ở Service
    Answer toEntity(AnswerRequest request);
}