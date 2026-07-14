package com.king.lms.e_learning_hub.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.king.lms.e_learning_hub.dto.Response.ApiResponse;
import com.king.lms.e_learning_hub.dto.quiz_result.QuizResultRequest;
import com.king.lms.e_learning_hub.dto.quiz_result.QuizResultResponse;
import com.king.lms.e_learning_hub.service.QuizResultService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@AllArgsConstructor
@RequestMapping("/quiz-result")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizResultController {

    QuizResultService quizResultService;

    /**
     * 1. Tạo mới kết quả quiz
     * POST /quiz-result
     */
    @PreAuthorize("hasAnyRole('user', 'admin')")
    @PostMapping
    public ApiResponse<QuizResultResponse> createQuizResult(@RequestBody @Valid QuizResultRequest request) {
        return ApiResponse.<QuizResultResponse>builder()
                .result(quizResultService.createQuizResult(request))
                .build();
    }

    /**
     * 2. Lấy chi tiết một kết quả quiz
     * GET /quiz-result/{quizResultId}
     */
    @GetMapping("/{quizResultId}")
    public ApiResponse<QuizResultResponse> getQuizResultById(@PathVariable Long quizResultId) {
        return ApiResponse.<QuizResultResponse>builder()
                .result(quizResultService.getQuizResultById(quizResultId))
                .build();
    }

    /**
     * 3. Lấy toàn bộ kết quả của một Quiz
     * GET /quiz-result/quiz/{quizId}
     */
    @GetMapping("/quiz/{quizId}")
    public ApiResponse<List<QuizResultResponse>> getResultsByQuizId(@PathVariable Long quizId) {
        return ApiResponse.<List<QuizResultResponse>>builder()
                .result(quizResultService.getResultsByQuizId(quizId))
                .build();
    }

    /**
     * 4. Lấy toàn bộ kết quả của một User
     * GET /quiz-result/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<QuizResultResponse>> getResultsByUserId(@PathVariable Long userId) {
        return ApiResponse.<List<QuizResultResponse>>builder()
                .result(quizResultService.getResultsByUserId(userId))
                .build();
    }

    /**
     * 5. Lấy kết quả gần nhất của user cho quiz
     * GET /quiz-result/latest/{quizId}/{userId}
     */
    @GetMapping("/latest/{quizId}/{userId}")
    public ApiResponse<QuizResultResponse> getLatestResultByQuizAndUser(
            @PathVariable Long quizId,
            @PathVariable Long userId) {
        return ApiResponse.<QuizResultResponse>builder()
                .result(quizResultService.getLatestResultByQuizAndUser(quizId, userId))
                .build();
    }

    /**
     * 6. Đếm số lần user làm quiz
     * GET /quiz-result/count/attempts/{quizId}/{userId}
     */
    @GetMapping("/count/attempts/{quizId}/{userId}")
    public ApiResponse<Long> getAttemptCountByQuizAndUser(
            @PathVariable Long quizId,
            @PathVariable Long userId) {
        return ApiResponse.<Long>builder()
                .result(quizResultService.getAttemptCountByQuizAndUser(quizId, userId))
                .build();
    }

    /**
     * 7. Đếm số user làm quiz
     * GET /quiz-result/count/users/{quizId}
     */
    @GetMapping("/count/users/{quizId}")
    public ApiResponse<Long> getUserCountByQuiz(@PathVariable Long quizId) {
        return ApiResponse.<Long>builder()
                .result(quizResultService.getUserCountByQuiz(quizId))
                .build();
    }

    /**
     * 8. Đếm số quiz user đã làm
     * GET /quiz-result/count/quizzes/{userId}
     */
    @GetMapping("/count/quizzes/{userId}")
    public ApiResponse<Long> getQuizCountByUser(@PathVariable Long userId) {
        return ApiResponse.<Long>builder()
                .result(quizResultService.getQuizCountByUser(userId))
                .build();
    }

    /**
     * 9. Xóa một kết quả quiz
     * DELETE /quiz-result/{quizResultId}
     */
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{quizResultId}")
    public ApiResponse<Void> deleteQuizResult(@PathVariable Long quizResultId) {
        quizResultService.deleteQuizResult(quizResultId);
        return ApiResponse.<Void>builder().build();
    }

    /**
     * 10. Xóa toàn bộ kết quả của một user
     * DELETE /quiz-result/user/{userId}
     */
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/user/{userId}")
    public ApiResponse<Void> deleteAllResultsByUserId(@PathVariable Long userId) {
        quizResultService.deleteAllResultsByUserId(userId);
        return ApiResponse.<Void>builder().build();
    }
}