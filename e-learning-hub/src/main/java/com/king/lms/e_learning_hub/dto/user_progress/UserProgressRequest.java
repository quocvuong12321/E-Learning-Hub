package com.king.lms.e_learning_hub.dto.user_progress;

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
public class UserProgressRequest {

    @NotNull(message = "User ID is required")
    Long userId;

    @NotNull(message = "Lesson ID is required")
    Long lessonId;

    Boolean isCompleted;

    Integer lastWatchedTime; // Tính bằng giây
}