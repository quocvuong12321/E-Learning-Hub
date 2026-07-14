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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.king.lms.e_learning_hub.dto.Response.ApiResponse;
import com.king.lms.e_learning_hub.dto.user_progress.UserProgressRequest;
import com.king.lms.e_learning_hub.dto.user_progress.UserProgressResponse;
import com.king.lms.e_learning_hub.dto.user_progress.UserProgressUpdateRequest;
import com.king.lms.e_learning_hub.service.UserProgressService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@AllArgsConstructor
@RequestMapping("/user-progress")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProgressController {

    UserProgressService userProgressService;

    /**
     * 1. Tạo mới tiến độ học tập
     * POST /user-progress
     */
    @PreAuthorize("hasAnyRole('user', 'admin')")
    @PostMapping
    public ApiResponse<UserProgressResponse> createUserProgress(@RequestBody @Valid UserProgressRequest request) {
        return ApiResponse.<UserProgressResponse>builder()
                .result(userProgressService.createUserProgress(request))
                .build();
    }

    /**
     * 2. Lấy chi tiết tiến độ học tập
     * GET /user-progress/{userProgressId}
     */
    @GetMapping("/{userProgressId}")
    public ApiResponse<UserProgressResponse> getUserProgressById(@PathVariable Long userProgressId) {
        return ApiResponse.<UserProgressResponse>builder()
                .result(userProgressService.getUserProgressById(userProgressId))
                .build();
    }

    /**
     * 3. Lấy toàn bộ tiến độ của một user
     * GET /user-progress/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<UserProgressResponse>> getProgressByUserId(@PathVariable Long userId) {
        return ApiResponse.<List<UserProgressResponse>>builder()
                .result(userProgressService.getProgressByUserId(userId))
                .build();
    }

    /**
     * 4. Lấy toàn bộ tiến độ của một lesson
     * GET /user-progress/lesson/{lessonId}
     */
    @GetMapping("/lesson/{lessonId}")
    public ApiResponse<List<UserProgressResponse>> getProgressByLessonId(@PathVariable Long lessonId) {
        return ApiResponse.<List<UserProgressResponse>>builder()
                .result(userProgressService.getProgressByLessonId(lessonId))
                .build();
    }

    /**
     * 5. Lấy tiến độ của user trong một lesson
     * GET /user-progress/{userId}/{lessonId}
     */
    @GetMapping("/{userId}/{lessonId}")
    public ApiResponse<UserProgressResponse> getProgressByUserAndLesson(
            @PathVariable Long userId,
            @PathVariable Long lessonId) {
        return ApiResponse.<UserProgressResponse>builder()
                .result(userProgressService.getProgressByUserAndLesson(userId, lessonId))
                .build();
    }

    /**
     * 6. Lấy số lesson hoàn thành của user
     * GET /user-progress/count/completed/user/{userId}
     */
    @GetMapping("/count/completed/user/{userId}")
    public ApiResponse<Long> getCompletedLessonCountByUser(@PathVariable Long userId) {
        return ApiResponse.<Long>builder()
                .result(userProgressService.getCompletedLessonCountByUser(userId))
                .build();
    }

    /**
     * 7. Lấy số user hoàn thành lesson
     * GET /user-progress/count/completed/lesson/{lessonId}
     */
    @GetMapping("/count/completed/lesson/{lessonId}")
    public ApiResponse<Long> getCompletedUserCountByLesson(@PathVariable Long lessonId) {
        return ApiResponse.<Long>builder()
                .result(userProgressService.getCompletedUserCountByLesson(lessonId))
                .build();
    }

    /**
     * 8. Cập nhật tiến độ xem và trạng thái hoàn thành
     * PUT /user-progress/{userProgressId}
     */
    @PreAuthorize("hasAnyRole('user', 'admin')")
    @PutMapping("/{userProgressId}")
    public ApiResponse<UserProgressResponse> updateUserProgress(
            @PathVariable Long userProgressId,
            @RequestBody @Valid UserProgressUpdateRequest request) {
        return ApiResponse.<UserProgressResponse>builder()
                .result(userProgressService.updateUserProgress(userProgressId, request))
                .build();
    }

    /**
     * 9. Cập nhật thời gian xem bài học
     * PUT /user-progress/{userProgressId}/watch-time
     */
    @PreAuthorize("hasAnyRole('user', 'admin')")
    @PutMapping("/{userProgressId}/watch-time")
    public ApiResponse<UserProgressResponse> updateLastWatchedTime(
            @PathVariable Long userProgressId,
            @RequestParam Integer watchedTime) {
        return ApiResponse.<UserProgressResponse>builder()
                .result(userProgressService.updateLastWatchedTime(userProgressId, watchedTime))
                .build();
    }

    /**
     * 10. Đánh dấu bài học đã hoàn thành
     * PUT /user-progress/{userProgressId}/complete
     */
    @PreAuthorize("hasAnyRole('user', 'admin')")
    @PutMapping("/{userProgressId}/complete")
    public ApiResponse<UserProgressResponse> completeLesson(@PathVariable Long userProgressId) {
        return ApiResponse.<UserProgressResponse>builder()
                .result(userProgressService.completeLesson(userProgressId))
                .build();
    }

    /**
     * 11. Xóa tiến độ học tập
     * DELETE /user-progress/{userProgressId}
     */
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{userProgressId}")
    public ApiResponse<Void> deleteUserProgress(@PathVariable Long userProgressId) {
        userProgressService.deleteUserProgress(userProgressId);
        return ApiResponse.<Void>builder().build();
    }

    /**
     * 12. Xóa toàn bộ tiến độ của user
     * DELETE /user-progress/user/{userId}
     */
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/user/{userId}")
    public ApiResponse<Void> deleteAllProgressByUserId(@PathVariable Long userId) {
        userProgressService.deleteAllProgressByUserId(userId);
        return ApiResponse.<Void>builder().build();
    }
}