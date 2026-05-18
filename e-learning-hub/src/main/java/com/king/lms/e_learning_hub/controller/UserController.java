package com.king.lms.e_learning_hub.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.king.lms.e_learning_hub.dto.Response.ApiResponse;
import com.king.lms.e_learning_hub.dto.Response.PageResponse;
import com.king.lms.e_learning_hub.dto.user.UserResponse;
import com.king.lms.e_learning_hub.service.UserService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    /**
     * Tìm kiếm và lọc user theo nhiều tiêu chí
     * Query parameters:
     * - search: tìm theo username, email, fullName, phoneNumber
     * - isActive: true/false/null (lấy tất cả)
     * - page: trang (mặc định 0)
     * - size: kích thước trang (mặc định 10)
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/search")
    public ApiResponse<PageResponse<UserResponse>> searchAndFilterUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "isActive", required = false) Boolean isActive) {

        return ApiResponse.<PageResponse<UserResponse>>builder()
                .result(userService.searchAndFilterUsers(page, size, search, isActive))
                .build();
    }

    /**
     * Lấy chi tiết user theo ID
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(id))
                .build();
    }

    /**
     * Deactivate user (khoá tài khoản)
     */
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/{id}/deactivate")
    public ApiResponse<UserResponse> deactivateUser(@PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.deactivateUser(id))
                .build();
    }

    /**
     * Activate user (mở khoá tài khoản)
     */
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/{id}/activate")
    public ApiResponse<UserResponse> activateUser(@PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.activateUser(id))
                .build();
    }

    // /**
    //  * Xóa user
    //  */
    // @DeleteMapping("/{id}")
    // public ApiResponse<Void> deleteUser(@PathVariable Long id) {
    //     userService.deleteUser(id);
    //     return ApiResponse.<Void>builder().build();
    // }

    // /**
    //  * Reset mật khẩu user
    //  */
    // @PostMapping("/{id}/reset-password")
    // public ApiResponse<Void> resetUserPassword(
    //         @PathVariable Long id,
    //         @RequestParam String newPassword) {

    //     userService.resetUserPassword(id, newPassword);
    //     return ApiResponse.<Void>builder().build();
    // }

    // /**
    //  * Lấy danh sách user hoạt động
    //  */
    // @GetMapping("/active/list")
    // public ApiResponse<PageResponse<UserResponse>> getActiveUsers(
    //         @RequestParam(value = "page", defaultValue = "0") int page,
    //         @RequestParam(value = "size", defaultValue = "10") int size) {

    //     return ApiResponse.<PageResponse<UserResponse>>builder()
    //             .result(userService.getActiveUsers(page, size))
    //             .build();
    // }

    /**
     * Đếm tổng số user
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/count/total")
    public ApiResponse<Long> countTotalUsers() {
        return ApiResponse.<Long>builder()
                .result(userService.countTotalUsers())
                .build();
    }

    /**
     * Đếm user hoạt động
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/count/active")
    public ApiResponse<Long> countActiveUsers() {
        return ApiResponse.<Long>builder()
                .result(userService.countActiveUsers())
                .build();
    }
}