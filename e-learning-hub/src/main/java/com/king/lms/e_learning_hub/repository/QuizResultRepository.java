package com.king.lms.e_learning_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.king.lms.e_learning_hub.entity.QuizResult;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    // Tìm theo Quiz
    List<QuizResult> findByQuizId(Long quizId);

    // Tìm theo User
    List<QuizResult> findByUserId(Long userId);

    // Tìm theo Quiz và User
    Optional<QuizResult> findByQuizIdAndUserId(Long quizId, Long userId);

    // Lấy kết quả gần nhất của user cho quiz
    @Query("SELECT qr FROM QuizResult qr WHERE qr.quiz.id = :quizId AND qr.user.id = :userId ORDER BY qr.createdAt DESC LIMIT 1")
    Optional<QuizResult> findLatestResultByQuizAndUser(@Param("quizId") Long quizId, @Param("userId") Long userId);

    // Đếm số lần user làm quiz
    Long countByQuizIdAndUserId(Long quizId, Long userId);

    // Đếm số user làm quiz
    Long countByQuizId(Long quizId);

    // Đếm số quiz user đã làm
    Long countByUserId(Long userId);
}