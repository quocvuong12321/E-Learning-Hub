package com.king.lms.e_learning_hub.dto.quiz;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.king.lms.e_learning_hub.dto.question.QuestionResponse;

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
public class QuizResponse {

    Long id;
    Long lessonId;
    String title;
    Double targetScore;
    List<QuestionResponse> questions;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}