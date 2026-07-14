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
import com.king.lms.e_learning_hub.dto.user_course.UserCourseRequest;
import com.king.lms.e_learning_hub.dto.user_course.UserCourseResponse;
import com.king.lms.e_learning_hub.dto.user_course.UserCourseUpdateRequest;
import com.king.lms.e_learning_hub.service.UserCourseService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@AllArgsConstructor
@RequestMapping("/user-course")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserCourseController {

    UserCourseService userCourseService;

    /**
     * 1. Tạo mới đăng ký khóa học (User tự đăng ký)
     * POST /user-course
     */
    // @PreAuthorize("hasAnyRole('user', 'admin')")
    @PostMapping
    public ApiResponse<UserCourseResponse> createUserCourse(@RequestBody @Valid UserCourseRequest request) {
        return ApiResponse.<UserCourseResponse>builder()
                .result(userCourseService.createUserCourse(request))
                .build();
    }

    /**
     * 2. Lấy chi tiết một đăng ký khóa học
     * GET /user-course/{userCourseId}
     */
    @GetMapping("/{userCourseId}")
    public ApiResponse<UserCourseResponse> getUserCourseById(@PathVariable Long userCourseId) {
        return ApiResponse.<UserCourseResponse>builder()
                .result(userCourseService.getUserCourseById(userCourseId))
                .build();
    }

    /**
     * 3. Lấy toàn bộ khóa học của User hiện tại
     * GET /user-course/my-courses
     */
    @GetMapping("/my-courses")
    public ApiResponse<List<UserCourseResponse>> getUserCoursesByUserId() {
        return ApiResponse.<List<UserCourseResponse>>builder()
                .result(userCourseService.getUserCoursesByUserId())
                .build();
    }

    /**
     * 4. Lấy toàn bộ Users đã đăng ký một Course
     * GET /user-course/course/{courseId}
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/course/{courseId}")
    public ApiResponse<List<UserCourseResponse>> getUsersByCourseId(@PathVariable Long courseId) {
        return ApiResponse.<List<UserCourseResponse>>builder()
                .result(userCourseService.getUsersByCourseId(courseId))
                .build();
    }

    /**
     * 5. Kiểm tra User hiện tại đã đăng ký Course chưa
     * GET /user-course/check/{courseId}
     */
    @PreAuthorize("hasAnyRole('customer', 'admin')")
    @GetMapping("/check/{courseId}")
    public ApiResponse<Boolean> isUserEnrolledInCourse(@PathVariable Long courseId) {
        return ApiResponse.<Boolean>builder()
                .result(userCourseService.isUserEnrolledInCourse(courseId))
                .build();
    }

    /**
     * 6. Lấy UserCourse của User hiện tại trong một Course
     * GET /user-course/my-course/{courseId}
     */
    @PreAuthorize("hasAnyRole('customer', 'admin')")
    @GetMapping("/my-course/{courseId}")
    public ApiResponse<UserCourseResponse> getUserCourseByUserAndCourse(@PathVariable Long courseId) {
        return ApiResponse.<UserCourseResponse>builder()
                .result(userCourseService.getUserCourseByUserAndCourse(courseId))
                .build();
    }

    /**
     * 7. Cập nhật trạng thái UserCourse (Admin only)
     * PUT /user-course/{userCourseId}
     */
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/{userCourseId}")
    public ApiResponse<UserCourseResponse> updateUserCourseStatus(
            @PathVariable Long userCourseId, 
            @RequestBody @Valid UserCourseUpdateRequest request) {
        return ApiResponse.<UserCourseResponse>builder()
                .result(userCourseService.updateUserCourseStatus(userCourseId, request))
                .build();
    }

    /**
     * 8. Đánh dấu khóa học của User hiện tại đã hoàn thành
     * PUT /user-course/{courseId}/complete
     */
    @PreAuthorize("hasAnyRole('customer', 'admin')")
    @PutMapping("/{courseId}/complete")
    public ApiResponse<UserCourseResponse> completeUserCourse(@PathVariable Long courseId) {
        return ApiResponse.<UserCourseResponse>builder()
                .result(userCourseService.completeUserCourse(courseId))
                .build();
    }

    /**
     * 9. Xóa một đăng ký khóa học (Admin only)
     * DELETE /user-course/{userCourseId}
     */
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{userCourseId}")
    public ApiResponse<Void> deleteUserCourse(@PathVariable Long userCourseId) {
        userCourseService.deleteUserCourse(userCourseId);
        return ApiResponse.<Void>builder().build();
    }

    /**
     * 10. User xóa đăng ký khóa học của chính mình
     * DELETE /user-course/my-course/{courseId}
     */
    @PreAuthorize("hasAnyRole('customer', 'admin')")
    @DeleteMapping("/my-course/{courseId}")
    public ApiResponse<Void> deleteUserCourseByUserAndCourse(@PathVariable Long courseId) {
        userCourseService.deleteUserCourseByUserAndCourse(courseId);
        return ApiResponse.<Void>builder().build();
    }

    /**
     * 11. Xóa toàn bộ đăng ký khóa học của một User (Admin only)
     * DELETE /user-course/user/{userId}
     */
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/user/{userId}")
    public ApiResponse<Void> deleteAllUserCoursesByUserId(@PathVariable Long userId) {
        userCourseService.deleteAllUserCoursesByUserId(userId);
        return ApiResponse.<Void>builder().build();
    }

    /**
     * 12. Lấy số lượng User đã đăng ký một Course
     * GET /user-course/count/course/{courseId}
     */
    @GetMapping("/count/course/{courseId}")
    public ApiResponse<Long> getEnrollmentCountByCourse(@PathVariable Long courseId) {
        return ApiResponse.<Long>builder()
                .result(userCourseService.getEnrollmentCountByCourse(courseId))
                .build();
    }

    /**
     * 13. Lấy số khóa học mà User hiện tại đã đăng ký
     * GET /user-course/count/my-courses
     */
    @PreAuthorize("hasAnyRole('customer', 'admin')")
    @GetMapping("/count/my-courses")
    public ApiResponse<Long> getEnrollmentCountByCurrentUser() {
        return ApiResponse.<Long>builder()
                .result(userCourseService.getEnrollmentCountByCurrentUser())
                .build();
    }

    /**
     * 14. Lấy số khóa học mà một User đã đăng ký (Admin only)
     * GET /user-course/count/user/{userId}
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/count/user/{userId}")
    public ApiResponse<Long> getEnrollmentCountByUser(@PathVariable Long userId) {
        return ApiResponse.<Long>builder()
                .result(userCourseService.getEnrollmentCountByUser(userId))
                .build();
    }
}