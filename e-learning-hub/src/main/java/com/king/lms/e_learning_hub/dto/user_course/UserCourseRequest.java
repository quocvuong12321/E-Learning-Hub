package com.king.lms.e_learning_hub.dto.user_course;

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
public class UserCourseRequest {

    @NotNull(message = "Course ID is required")
    Long courseId;

    Long orderId; // Có thể null nếu là khóa học miễn phí

    @NotNull(message = "Enrollment type is required")
    String enrollmentType; // FREE, PAID
}