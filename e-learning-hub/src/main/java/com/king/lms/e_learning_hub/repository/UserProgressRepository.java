package com.king.lms.e_learning_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.lms.e_learning_hub.entity.UserProgress;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    // Tìm theo User
    List<UserProgress> findByUserId(Long userId);

    // Tìm theo Lesson
    List<UserProgress> findByLessonId(Long lessonId);

    // Tìm theo User và Lesson
    Optional<UserProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    // Đếm số lesson hoàn thành của user
    Long countByUserIdAndIsCompletedTrue(Long userId);

    // Đếm số user hoàn thành lesson
    Long countByLessonIdAndIsCompletedTrue(Long lessonId);

    // Xóa theo User
    void deleteByUserId(Long userId);
}