package com.king.lms.e_learning_hub.dto.user_course;

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
public class UserCourseResponse {

    Long id;
    Long userId;
    Long courseId;
    Long orderId;
    String enrollmentType;
    String status;
    LocalDateTime enrolledAt;
    LocalDateTime completedAt;
    LocalDateTime expiredAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}