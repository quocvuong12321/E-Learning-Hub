package com.king.lms.e_learning_hub.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.king.lms.e_learning_hub.dto.Response.PageResponse;
import com.king.lms.e_learning_hub.dto.course.CourseRequest;
import com.king.lms.e_learning_hub.dto.course.CourseResponse;
import com.king.lms.e_learning_hub.dto.course.CourseSummaryProjection;
import com.king.lms.e_learning_hub.entity.Course;
import com.king.lms.e_learning_hub.entity.CoursesCategory;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.king.lms.e_learning_hub.mapper.CourseMapper;
import com.king.lms.e_learning_hub.repository.CourseCategoryRepository;
import com.king.lms.e_learning_hub.repository.CourseRepository;
import com.king.lms.e_learning_hub.util.FileUploadUtils;
import com.king.lms.e_learning_hub.util.JwtUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseService {
    CourseRepository courseRepository;
    CourseMapper courseMapper;
    CourseCategoryRepository courseCategoryRepository;
    FileUploadUtils fileUploadUtils;
    JwtUtils jwtUtils;


    public CourseResponse getCourseBySlugOrId(String identifier) {
        Course course;
        try {
            Long id = Long.parseLong(identifier);
            course = courseRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));
        } catch (NumberFormatException e) {
            course = courseRepository.findBySlug(identifier)
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));
        }
        
        return courseMapper.toResponse(course);
    }

    /**
     * Tìm kiếm và lọc khóa học với đầy đủ tiêu chí
     * - Tự động kiểm tra quyền: non-admin chỉ lấy course published
     * - Hỗ trợ search, filter theo category, isFree, hasDiscount, rangePrice
     */
    public PageResponse<CourseSummaryProjection> searchAndFilterCourses(int page, int size, String search,
                                                                        Long categoryId, Boolean isFree,
                                                                        Boolean hasDiscount, BigDecimal minPrice,
                                                                        BigDecimal maxPrice) {
        // Kiểm tra quyền
        List<String> roles = jwtUtils.getRoleByAuthentication();
        Boolean isPublished = null;

        if (!roles.contains("ROLE_admin")) {
            isPublished = true; // Non-admin chỉ lấy course published
        }

        // Xử lý search string rỗng
        if (search != null && search.trim().isEmpty()) {
            search = null;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CourseSummaryProjection> result = courseRepository.searchAndFilterCourses(
                search, categoryId, isPublished, isFree, hasDiscount, minPrice, maxPrice, pageable);

        return PageResponse.<CourseSummaryProjection>builder()
                .currentPage(page)
                .totalPages(result.getTotalPages())
                .pageSize(result.getSize())
                .totalElements(result.getTotalElements())
                .data(result.getContent())
                .build();
    }

   
    /**
     * Tạo khóa học mới
     */
    @Transactional
    public CourseResponse createCourse(CourseRequest request, MultipartFile thumbnail) {
        // Kiểm tra slug đã tồn tại
        if (courseRepository.existsBySlug(request.getSlug())) {
            throw new AppException(ErrorCode.SLUG_EXISTED);
        }

        // Tìm category
        CoursesCategory category = courseCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));

        // Map request thành entity
        Course course = courseMapper.toCourse(request);
        course.setCategory(category);

        // Lưu thumbnail nếu có
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailUrl = fileUploadUtils.saveImage(thumbnail, request.getSlug());
            course.setThumbnail(thumbnailUrl);
        }

        return courseMapper.toResponse(courseRepository.save(course));
    }

    /**
     * Cập nhật khóa học
     */
    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request, MultipartFile thumbnail) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));

        // Tìm category
        CoursesCategory category = courseCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));

        // Update các trường
        courseMapper.updateCourse(request, course);
        course.setCategory(category);

        // Xử lý thumbnail
        if (thumbnail != null && !thumbnail.isEmpty()) {
            // Xóa thumbnail cũ
            if (course.getThumbnail() != null && !course.getThumbnail().isEmpty()) {
                fileUploadUtils.deleteImage(course.getThumbnail());
            }
            // Lưu thumbnail mới
            String thumbnailUrl = fileUploadUtils.saveImage(thumbnail, request.getSlug());
            course.setThumbnail(thumbnailUrl);
        }

        return courseMapper.toResponse(courseRepository.save(course));
    }

    /**
     * Lấy chi tiết khóa học theo ID
     */
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));
        return courseMapper.toResponse(course);
    }

    /**
     * Lấy chi tiết khóa học theo slug
     */
    public CourseResponse getCourseBySlug(String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));
        return courseMapper.toResponse(course);
    }

    /**
     * Xóa khóa học
     */
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));

        // Xóa thumbnail
        if (course.getThumbnail() != null && !course.getThumbnail().isEmpty()) {
            fileUploadUtils.deleteImage(course.getThumbnail());
        }

        // Xóa toàn bộ thư mục
        fileUploadUtils.deleteImageFolder(course.getSlug());

        courseRepository.delete(course);
    }

/**
     * Công khai khóa học (publish)
     */
@Transactional
public CourseResponse publishCourse(Long id) {
    Course course = courseRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));
    
    course.setIsPublished(true);
    return courseMapper.toResponse(courseRepository.save(course));
}

/**
 * Ẩn khóa học (unpublish)
 */
@Transactional
public CourseResponse unpublishCourse(Long id) {
    Course course = courseRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));
    
    course.setIsPublished(false);
    return courseMapper.toResponse(courseRepository.save(course));
}
  
   
    // /**
    //  * Đếm số khóa học theo category
    //  */
    // public Long countCoursesByCategory(Long categoryId) {
    //     return courseRepository.countByCategoryId(categoryId);
    // }

    // /**
    //  * Đếm số khóa học miễn phí
    //  */
    // public Long countFreeCourses() {
    //     return courseRepository.countByIsFreeTrueAndIsPublishedTrue();
    // }
}