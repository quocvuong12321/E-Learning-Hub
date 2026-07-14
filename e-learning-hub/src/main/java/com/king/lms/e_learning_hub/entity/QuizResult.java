package com.king.lms.e_learning_hub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "quiz_result",
    indexes = {
        @Index(name = "idx_quiz_user", columnList = "quiz_id,user_id")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "started_at", nullable = false)
    LocalDateTime startedAt;

    @Column(name = "total_corrected_questions", nullable = false)
    Integer totalCorrectAnswers;

    @Column(name = "total_questions", nullable = false)
    Integer totalQuestions;

    @Column(name = "score")
    Double score;

    @Column(name = "is_pass")
    Boolean isPass;

    /**
     * Helper method: Tính điểm phần trăm (nếu chưa có field score)
     */
    public Double getScorePercentage() {
        if (totalQuestions == null || totalQuestions == 0) {
            return 0.0;
        }
        return (double) (totalCorrectAnswers * 100) / totalQuestions;
    }

    /**
     * Helper method: Tính thời gian làm bài
     */
    public Long getDurationInSeconds() {
        if (startedAt == null) {
            return 0L;
        }
        return java.time.temporal.ChronoUnit.SECONDS.between(startedAt, LocalDateTime.now());
    }
}