package com.king.lms.e_learning_hub.dto.quiz_result;

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
public class QuizResultResponse {

    Long id;
    Long quizId;
    Long userId;
    LocalDateTime startedAt;
    Integer totalCorrectAnswers;
    Integer totalQuestions;
    Double score;
    Boolean isPass;
    Double scorePercentage; // Helper field
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}