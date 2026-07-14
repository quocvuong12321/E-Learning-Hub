package com.king.lms.e_learning_hub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.lms.e_learning_hub.dto.quiz_result.QuizResultRequest;
import com.king.lms.e_learning_hub.dto.quiz_result.QuizResultResponse;
import com.king.lms.e_learning_hub.entity.Quiz;
import com.king.lms.e_learning_hub.entity.QuizResult;
import com.king.lms.e_learning_hub.entity.User;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.king.lms.e_learning_hub.mapper.QuizResultMapper;
import com.king.lms.e_learning_hub.repository.QuizRepository;
import com.king.lms.e_learning_hub.repository.QuizResultRepository;
import com.king.lms.e_learning_hub.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizResultService {

    QuizResultRepository quizResultRepository;
    QuizRepository quizRepository;
    UserRepository userRepository;
    QuizResultMapper quizResultMapper;

    // ==========================================
    // HÀM CREATE (Tạo mới)
    // ==========================================

    /**
     * Tạo mới kết quả quiz
     */
    @Transactional
    public QuizResultResponse createQuizResult(QuizResultRequest request) {
        
        // Kiểm tra Quiz tồn tại
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXIST));

        // Kiểm tra User tồn tại
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        // Map từ request sang entity
        QuizResult quizResult = quizResultMapper.toEntity(request);
        quizResult.setQuiz(quiz);
        quizResult.setUser(user);
        quizResult.setStartedAt(LocalDateTime.now());

        // Tính toán score và isPass
        Double scorePercentage = calculateScorePercentage(
            request.getTotalCorrectAnswers(), 
            request.getTotalQuestions()
        );
        quizResult.setScore(scorePercentage);
        quizResult.setIsPass(scorePercentage >= quiz.getTargetScore());

        // Lưu và trả về response
        QuizResult saved = quizResultRepository.save(quizResult);
        return mapToResponseWithPercentage(saved);
    }

    // ==========================================
    // HÀM GET (Lấy chi tiết / danh sách)
    // ==========================================

    /**
     * Lấy chi tiết một kết quả quiz
     */
    public QuizResultResponse getQuizResultById(Long quizResultId) {
        QuizResult quizResult = quizResultRepository.findById(quizResultId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_RESULT_NOT_EXIST));
        return mapToResponseWithPercentage(quizResult);
    }

    /**
     * Lấy toàn bộ kết quả của một Quiz
     */
    public List<QuizResultResponse> getResultsByQuizId(Long quizId) {
        quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXIST));

        List<QuizResult> results = quizResultRepository.findByQuizId(quizId);
        return results.stream()
                .map(this::mapToResponseWithPercentage)
                .collect(Collectors.toList());
    }

    /**
     * Lấy toàn bộ kết quả của một User
     */
    public List<QuizResultResponse> getResultsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        List<QuizResult> results = quizResultRepository.findByUserId(userId);
        return results.stream()
                .map(this::mapToResponseWithPercentage)
                .collect(Collectors.toList());
    }

    /**
     * Lấy kết quả gần nhất của user cho quiz
     */
    public QuizResultResponse getLatestResultByQuizAndUser(Long quizId, Long userId) {
        QuizResult quizResult = quizResultRepository.findLatestResultByQuizAndUser(quizId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_RESULT_NOT_EXIST));
        return mapToResponseWithPercentage(quizResult);
    }

    /**
     * Đếm số lần user làm quiz
     */
    public Long getAttemptCountByQuizAndUser(Long quizId, Long userId) {
        return quizResultRepository.countByQuizIdAndUserId(quizId, userId);
    }

    /**
     * Đếm số user làm quiz
     */
    public Long getUserCountByQuiz(Long quizId) {
        return quizResultRepository.countByQuizId(quizId);
    }

    /**
     * Đếm số quiz user đã làm
     */
    public Long getQuizCountByUser(Long userId) {
        return quizResultRepository.countByUserId(userId);
    }

    // ==========================================
    // HÀM DELETE (Xóa)
    // ==========================================

    /**
     * Xóa một kết quả quiz
     */
    @Transactional
    public void deleteQuizResult(Long quizResultId) {
        QuizResult quizResult = quizResultRepository.findById(quizResultId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_RESULT_NOT_EXIST));
        quizResultRepository.delete(quizResult);
    }

    /**
     * Xóa toàn bộ kết quả của một user
     */
    @Transactional
    public void deleteAllResultsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        List<QuizResult> results = quizResultRepository.findByUserId(userId);
        quizResultRepository.deleteAll(results);
    }

    // ==========================================
    // HÀM HELPER
    // ==========================================

    /**
     * Tính % điểm
     */
    private Double calculateScorePercentage(Integer correctAnswers, Integer totalQuestions) {
        if (totalQuestions == null || totalQuestions == 0) {
            return 0.0;
        }
        return (double) (correctAnswers * 100) / totalQuestions;
    }

    /**
     * Map entity sang response với scorePercentage
     */
    private QuizResultResponse mapToResponseWithPercentage(QuizResult quizResult) {
        QuizResultResponse response = quizResultMapper.toResponse(quizResult);
        response.setScorePercentage(quizResult.getScorePercentage());
        return response;
    }
}