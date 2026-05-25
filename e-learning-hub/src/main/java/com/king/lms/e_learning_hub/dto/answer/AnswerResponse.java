package com.king.lms.e_learning_hub.dto.answer;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerResponse {

    Long id;
    Long questionId;
    String answerText;
    Boolean isCorrect;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}