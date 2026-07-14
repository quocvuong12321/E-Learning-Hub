package com.king.lms.e_learning_hub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
    name = "user_progress",
    indexes = {
        @Index(name = "idx_user_lesson", columnList = "user_id,lesson_id")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProgress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    Lesson lesson;

    @Column(name = "is_completed")
    Boolean isCompleted = false;

    @Column(name = "last_watched_time")
    Integer lastWatchedTime = 0; // Tính bằng giây

    /**
     * Helper method: Tính % tiến độ xem
     * (Giả sử lesson có tổng thời lượng video)
     */
    public Double getProgressPercentage(Integer totalLessonDuration) {
        if (totalLessonDuration == null || totalLessonDuration == 0) {
            return 0.0;
        }
        return (double) (lastWatchedTime * 100) / totalLessonDuration;
    }

    /**
     * Helper method: Kiểm tra user đã xem qua 50% bài học chưa
     */
    public Boolean isHalfWatched(Integer totalLessonDuration) {
        if (totalLessonDuration == null || totalLessonDuration == 0) {
            return false;
        }
        return lastWatchedTime >= (totalLessonDuration / 2);
    }
}