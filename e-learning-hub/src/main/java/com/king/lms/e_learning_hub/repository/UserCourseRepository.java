package com.king.lms.e_learning_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.lms.e_learning_hub.entity.UserCourse;

public interface UserCourseRepository extends JpaRepository<UserCourse,Long> {
     // Tìm theo User
    List<UserCourse> findByUserId(Long userId);

    // Tìm theo Course
    List<UserCourse> findByCourseId(Long courseId);

    // Tìm theo User và Course
    Optional<UserCourse> findByUserIdAndCourseId(Long userId, Long courseId);

    // Đếm theo Course
    Long countByCourseId(Long courseId);

    // Đếm theo User
    Long countByUserId(Long userId);

    // Xóa theo User
    void deleteByUserId(Long userId);
}
