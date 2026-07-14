package com.king.lms.e_learning_hub.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;


@Entity
@Table(
    name = "users_courses",
    indexes = {
        @Index(name = "idx_user_course", columnList = "user_id,course_id")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCourse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    Order order; // Có thể null nếu là khóa học miễn phí

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "enrollment_type", nullable = false)
    EnrollmentType enrollmentType = EnrollmentType.FREE;

    @Column(name = "enrolled_at", nullable = false)
    LocalDateTime enrolledAt;

    @Column(name = "completed_at")
    LocalDateTime completedAt;

    @Column(name = "expired_at")
    LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    UserCourseStatus status = UserCourseStatus.ACTIVE;

    /**
     * Enum cho kiểu đăng ký khóa học
     */
    public enum EnrollmentType {
        FREE,
        PAID
    }

    /**
     * Enum cho trạng thái đăng ký
     */
    public enum UserCourseStatus {
        ACTIVE,
        EXPIRED
    }

    /**
     * Helper method: Kiểm tra khóa học còn hiệu lực không
     */
    public Boolean isExpired() {
        if (expiredAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expiredAt);
    }

    /**
     * Helper method: Kiểm tra khóa học đã hoàn thành không
     */
    public Boolean isCompleted() {
        return completedAt != null;
    }
}