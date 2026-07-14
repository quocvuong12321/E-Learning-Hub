package com.king.lms.e_learning_hub.dto.user_progress;

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
public class UserProgressUpdateRequest {

    Boolean isCompleted;

    Integer lastWatchedTime; // Tính bằng giây
}