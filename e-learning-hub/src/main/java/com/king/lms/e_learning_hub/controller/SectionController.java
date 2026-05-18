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
import com.king.lms.e_learning_hub.dto.section.SectionRequest;
import com.king.lms.e_learning_hub.dto.section.SectionResponse;
import com.king.lms.e_learning_hub.service.SectionService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@AllArgsConstructor
@RequestMapping("/section")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SectionController {

    SectionService sectionService;

    @PreAuthorize("hasRole('admin')")
    @PostMapping
    public ApiResponse<SectionResponse> createSection(@RequestBody @Valid SectionRequest request) {
        return ApiResponse.<SectionResponse>builder()
                .result(sectionService.createSection(request))
                .build();
    }

    @PreAuthorize("hasRole('admin')")
    @PutMapping("/{id}")
    public ApiResponse<SectionResponse> updateSection(
            @PathVariable Long id, 
            @RequestBody @Valid SectionRequest request) {
        return ApiResponse.<SectionResponse>builder()
                .result(sectionService.updateSection(id, request))
                .build();
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/{id}")
    public ApiResponse<SectionResponse> getSectionById(@PathVariable Long id) {
        return ApiResponse.<SectionResponse>builder()
                .result(sectionService.getSectionById(id))
                .build();
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<SectionResponse>> getSectionsByCourse(@PathVariable Long courseId) {
        return ApiResponse.<List<SectionResponse>>builder()
                .result(sectionService.getSectionsByCourseId(courseId))
                .build();
    }
}