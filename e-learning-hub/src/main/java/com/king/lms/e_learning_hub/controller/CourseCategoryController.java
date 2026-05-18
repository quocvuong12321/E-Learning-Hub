package com.king.lms.e_learning_hub.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
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
import com.king.lms.e_learning_hub.dto.Response.PageResponse;
import com.king.lms.e_learning_hub.dto.category.CategoryRequest;
import com.king.lms.e_learning_hub.dto.category.CategoryResponse;
import com.king.lms.e_learning_hub.service.CourseCategoryService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/course-category")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class CourseCategoryController {
    CourseCategoryService courseCategoryService;

    @GetMapping
    public ApiResponse<PageResponse<CategoryResponse>> getCoursesCategory(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page) {
        return ApiResponse.<PageResponse<CategoryResponse>>builder()
                .result(courseCategoryService.getCategories(page, size))
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<Set<CategoryResponse>> getAllCoursesCategory() {
        return ApiResponse.<Set<CategoryResponse>>builder()
                .result(courseCategoryService.getCategories())
                .build();
    }
    @PreAuthorize("hasRole('admin')")
    @PostMapping
    public ApiResponse<CategoryResponse> createCoursesCategory(
            @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(courseCategoryService.createCategory(request))
                .build();
    }
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCoursesCategory(
            @PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(courseCategoryService.updateCategory(id, request))
                .build();
    }
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCoursesCategory(@PathVariable Long id) {
        courseCategoryService.deleteCategory(id);
        return ApiResponse.<Void>builder()
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCoursesCategory(@PathVariable Long id) {
        return ApiResponse.<CategoryResponse>builder()
                .result(courseCategoryService.getCategoryById(id))
                .build();
    }
}