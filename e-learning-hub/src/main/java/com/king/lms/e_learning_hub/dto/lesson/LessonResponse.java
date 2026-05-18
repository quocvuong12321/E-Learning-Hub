package com.king.lms.e_learning_hub.dto.lesson;

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
public class LessonResponse {

    Long id;

    String title;

    String videoUrl;

    Long durationSeconds;

    String content;

    Boolean isPreview;

    Integer orderIndex;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}