package com.king.lms.e_learning_hub.controller;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.king.lms.e_learning_hub.dto.Response.ApiResponse;
import com.king.lms.e_learning_hub.dto.Response.PageResponse;
import com.king.lms.e_learning_hub.dto.course.CourseRequest;
import com.king.lms.e_learning_hub.dto.course.CourseResponse;
import com.king.lms.e_learning_hub.dto.course.CourseSummaryProjection;
import com.king.lms.e_learning_hub.service.CourseService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@RestController
@AllArgsConstructor
@RequestMapping("/course")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseController {

    CourseService courseService;


    

    /**
     * Tạo khóa học mới
     */
    @PreAuthorize("hasRole('admin')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CourseResponse> createCourse(
            @RequestPart("data") CourseRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {

        return ApiResponse.<CourseResponse>builder()
                .result(courseService.createCourse(request, thumbnail))
                .build();
    }

    /**
     * Cập nhật khóa học
     */
    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CourseResponse> updateCourse(
            @PathVariable Long id,
            @RequestPart("data") CourseRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {

        return ApiResponse.<CourseResponse>builder()
                .result(courseService.updateCourse(id, request, thumbnail))
                .build();
    }

    /**
     * Xóa khóa học
     */
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ApiResponse.<Void>builder().build();
    }

     /**
     * Công khai khóa học
     */
     @PreAuthorize("hasRole('admin')")
     @PatchMapping("/{id}/publish")
     public ApiResponse<CourseResponse> publishCourse(@PathVariable Long id) {
         return ApiResponse.<CourseResponse>builder()
                 .result(courseService.publishCourse(id))
                 .build();
     }
 
     /**
      * Ẩn khóa học
      */
     @PreAuthorize("hasRole('admin')")
     @PatchMapping("/{id}/unpublish")
     public ApiResponse<CourseResponse> unpublishCourse(@PathVariable Long id) {
         return ApiResponse.<CourseResponse>builder()
                 .result(courseService.unpublishCourse(id))
                 .build();
     }

    /**
     * Tìm kiếm và lọc khóa học
     */
    @GetMapping("/search")
    public ApiResponse<PageResponse<CourseSummaryProjection>> searchAndFilterCourses(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "isFree", required = false) Boolean isFree,
            @RequestParam(value = "hasDiscount", required = false) Boolean hasDiscount,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice) {

        return ApiResponse.<PageResponse<CourseSummaryProjection>>builder()
                .result(courseService.searchAndFilterCourses(page, size, search, categoryId, isFree, hasDiscount, minPrice, maxPrice))
                .build();
    }



    @GetMapping("/{identifier}")
    public ApiResponse<CourseResponse> getCourseDetail(@PathVariable String identifier) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.getCourseBySlugOrId(identifier))
                .build();
    }


}