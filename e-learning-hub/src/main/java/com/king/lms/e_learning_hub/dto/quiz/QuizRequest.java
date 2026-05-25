package com.king.lms.e_learning_hub.dto.quiz;

import java.util.List;

import com.king.lms.e_learning_hub.dto.question.QuestionRequest;

import jakarta.validation.constraints.NotBlank;
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
public class QuizRequest {

    @NotNull(message = "Lesson ID is required")
    Long lessonId;

    @NotBlank(message = "Title is required")
    String title;

    @Builder.Default
    Double targetScore = 0.0;

    List<QuestionRequest> questionRequests;
}