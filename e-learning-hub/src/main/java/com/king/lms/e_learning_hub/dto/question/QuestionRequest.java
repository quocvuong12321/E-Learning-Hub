package com.king.lms.e_learning_hub.dto.question;

import java.util.List;

import com.king.lms.e_learning_hub.dto.answer.AnswerRequest;
import com.king.lms.e_learning_hub.enums.QuestionType;

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
public class QuestionRequest {


    @NotBlank(message = "Question text is required")
    String questionText;

    @NotNull(message = "Question type is required")
    QuestionType questionType;

    List<AnswerRequest> answerRequests;
}