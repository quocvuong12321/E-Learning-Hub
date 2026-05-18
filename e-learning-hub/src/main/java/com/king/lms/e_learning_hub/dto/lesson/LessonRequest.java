package com.king.lms.e_learning_hub.dto.lesson;

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
public class LessonRequest {

    @NotBlank(message = "Title is required")
    String title;

    String videoUrl;

    @Builder.Default
    Long durationSeconds = 0L;

    String content;

    @Builder.Default
    Boolean isPreview = false;

    @Builder.Default
    Integer orderIndex = 0;

    @NotNull(message = "Section ID is required")
    Long sectionId;
}