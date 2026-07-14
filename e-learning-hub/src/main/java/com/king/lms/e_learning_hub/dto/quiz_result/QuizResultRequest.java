package com.king.lms.e_learning_hub.dto.quiz_result;

import jakarta.validation.constraints.NotNull;
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
public class QuizResultRequest {

    @NotNull(message = "Quiz ID is required")
    Long quizId;

    @NotNull(message = "User ID is required")
    Long userId;

    @NotNull(message = "Total correct answers is required")
    Integer totalCorrectAnswers;

    @NotNull(message = "Total questions is required")
    Integer totalQuestions;
}