package com.king.lms.e_learning_hub.dto.question;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.king.lms.e_learning_hub.dto.answer.AnswerResponse;
import com.king.lms.e_learning_hub.enums.QuestionType;

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
public class QuestionResponse {

    Long id;
    Long quizId;
    String questionText;
    QuestionType questionType;
    List<AnswerResponse> answers;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}