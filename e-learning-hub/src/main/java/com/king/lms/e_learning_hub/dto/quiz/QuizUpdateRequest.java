package com.king.lms.e_learning_hub.dto.quiz;

import com.google.auto.value.AutoValue.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizUpdateRequest {

    @NotNull(message = "Lesson ID is required")
    Long lessonId;

    @NotBlank(message = "Title is required")
    String title;

    Double targetScore;
}