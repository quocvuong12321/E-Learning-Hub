package com.king.lms.e_learning_hub.dto.section;

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
public class SectionRequest {

    @NotBlank(message = "Title is required")
    String title;

    @Builder.Default
    Integer orderIndex = 0;

    @NotNull(message = "Course ID is required")
    Long courseId;
}