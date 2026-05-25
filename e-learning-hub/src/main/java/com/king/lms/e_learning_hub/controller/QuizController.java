package com.king.lms.e_learning_hub.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.king.lms.e_learning_hub.dto.Response.ApiResponse;
import com.king.lms.e_learning_hub.dto.question.QuestionRequest;
import com.king.lms.e_learning_hub.dto.question.QuestionResponse;
import com.king.lms.e_learning_hub.dto.quiz.QuizRequest;
import com.king.lms.e_learning_hub.dto.quiz.QuizResponse;
import com.king.lms.e_learning_hub.dto.quiz.QuizUpdateRequest;
import com.king.lms.e_learning_hub.service.QuizService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@AllArgsConstructor
@RequestMapping("/quiz")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizController {

    QuizService quizService;

    /**
     * 1. Tạo 1 Quiz hoàn chỉnh cùng toàn bộ Question và Answer
     * POST /quiz
     */
    @PreAuthorize("hasRole('admin')")
    @PostMapping
    public ApiResponse<QuizResponse> createFullQuiz(@RequestBody @Valid QuizRequest request) {
        return ApiResponse.<QuizResponse>builder()
                .result(quizService.createFullQuiz(request))
                .build();
    }

    /**
     * 2. Cập nhật thông tin cơ bản của Quiz (Tiêu đề, điểm chuẩn, lessonId)
     * PUT /quiz/{quizId}
     */
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/{quizId}")
    public ApiResponse<QuizResponse> updateQuiz(
            @PathVariable Long quizId, 
            @RequestBody @Valid QuizUpdateRequest request) {
        return ApiResponse.<QuizResponse>builder()
                .result(quizService.updateQuiz(quizId, request))
                .build();
    }

    /**
     * 3. Xoá toàn bộ Quiz (Cascade xoá hết Question và Answer)
     * DELETE /quiz/{quizId}
     */
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{quizId}")
    public ApiResponse<Void> deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ApiResponse.<Void>builder().build();
    }

    /**
     * 4. Thêm một câu hỏi (cùng câu trả lời) vào một Quiz có sẵn
     * POST /quiz/{quizId}/question
     */
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/{quizId}/question")
    public ApiResponse<QuestionResponse> addQuestionToQuiz(
            @PathVariable Long quizId, 
            @RequestBody @Valid QuestionRequest request) {
        return ApiResponse.<QuestionResponse>builder()
                .result(quizService.addQuestionToQuiz(quizId, request))
                .build();
    }

    /**
     * 5. Sửa một câu hỏi (và làm mới danh sách câu trả lời của nó)
     * PUT /quiz/question/{questionId}
     */
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/question/{questionId}")
    public ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long questionId, 
            @RequestBody @Valid QuestionRequest request) {
        return ApiResponse.<QuestionResponse>builder()
                .result(quizService.updateQuestion(questionId, request))
                .build();
    }

    /**
     * 6. Xoá 1 câu hỏi (Cascade xoá các câu trả lời của nó)
     * DELETE /quiz/question/{questionId}
     */
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/question/{questionId}")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long questionId) {
        quizService.deleteQuestion(questionId);
        return ApiResponse.<Void>builder().build();
    }

    /**
     * 7. Lấy chi tiết một Quiz (kèm list questions và answers)
     * GET /quiz/{quizId}
     */
    @GetMapping("/{quizId}")
    public ApiResponse<QuizResponse> getQuizById(@PathVariable Long quizId) {
        return ApiResponse.<QuizResponse>builder()
                .result(quizService.getQuizById(quizId))
                .build();
    }

    /**
     * 8. Lấy toàn bộ danh sách Quizzes của một Lesson
     * GET /quiz/lesson/{lessonId}
     */
    @GetMapping("/lesson/{lessonId}")
    public ApiResponse<List<QuizResponse>> getQuizzesByLessonId(@PathVariable Long lessonId) {
        return ApiResponse.<List<QuizResponse>>builder()
                .result(quizService.getQuizzesByLessonId(lessonId))
                .build();
    }

    /**
     * 9. Lấy chi tiết một Câu hỏi
     * GET /quiz/question/{questionId}
     */
    @GetMapping("/question/{questionId}")
    public ApiResponse<QuestionResponse> getQuestionById(@PathVariable Long questionId) {
        return ApiResponse.<QuestionResponse>builder()
                .result(quizService.getQuestionById(questionId))
                .build();
    }
}