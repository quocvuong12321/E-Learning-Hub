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
import com.king.lms.e_learning_hub.dto.lesson.LessonRequest;
import com.king.lms.e_learning_hub.dto.lesson.LessonResponse;
import com.king.lms.e_learning_hub.service.LessonService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@AllArgsConstructor
@RequestMapping("/lesson")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonController {

    LessonService lessonService;

    @PreAuthorize("hasRole('admin')")
    @PostMapping
    public ApiResponse<LessonResponse> createLesson(@RequestBody @Valid LessonRequest request) {
        return ApiResponse.<LessonResponse>builder()
                .result(lessonService.createLesson(request))
                .build();
    }

    @PreAuthorize("hasRole('admin')")
    @PutMapping("/{id}")
    public ApiResponse<LessonResponse> updateLesson(
            @PathVariable Long id, 
            @RequestBody @Valid LessonRequest request) {
        return ApiResponse.<LessonResponse>builder()
                .result(lessonService.updateLesson(id, request))
                .build();
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/{id}")
    public ApiResponse<LessonResponse> getLessonById(@PathVariable Long id) {
        return ApiResponse.<LessonResponse>builder()
                .result(lessonService.getLessonById(id))
                .build();
    }

    @GetMapping("/section/{sectionId}")
    public ApiResponse<List<LessonResponse>> getLessonsBySection(@PathVariable Long sectionId) {
        return ApiResponse.<List<LessonResponse>>builder()
                .result(lessonService.getLessonsBySectionId(sectionId))
                .build();
    }
}