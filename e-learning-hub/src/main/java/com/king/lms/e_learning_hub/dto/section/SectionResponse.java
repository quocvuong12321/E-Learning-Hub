package com.king.lms.e_learning_hub.dto.section;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.king.lms.e_learning_hub.dto.lesson.LessonResponse;

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
public class SectionResponse {

    Long id;

    String title;

    Integer orderIndex;

    Long courseId;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
    
    List<LessonResponse> lessons;
}