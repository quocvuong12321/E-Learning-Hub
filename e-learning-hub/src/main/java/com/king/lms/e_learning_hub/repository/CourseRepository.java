package com.king.lms.e_learning_hub.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.king.lms.e_learning_hub.dto.course.CourseSummaryProjection;
import com.king.lms.e_learning_hub.entity.Course;

import io.lettuce.core.dynamic.annotation.Param;

public interface CourseRepository extends JpaRepository<Course,Long> {

    boolean existsBySlug(String slug);

    Optional<Course> findBySlug(String slug);

    /**
     * Tìm kiếm course theo tiêu chí: title, category, published status
     * Trả về summary projection
     */
     /**
     * Tìm kiếm và lọc course theo tất cả tiêu chí
     * Trả về summary projection
     */
     @Query("""
        SELECT c.id as id,
               c.title as title,
               c.thumbnail as thumbnail,
               c.discount as discount,
               c.price as price,
               c.isFree as isFree,
               c.slug as slug,
               c.isPublished as isPublished,
               c.category.id as categoryId,
               c.category.name as categoryName,
               c.createdAt as createdAt
        FROM Course c
        WHERE (:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:categoryId IS NULL OR c.category.id = :categoryId)
          AND (:isPublished IS NULL OR c.isPublished = :isPublished)
          AND (:isFree IS NULL OR c.isFree = :isFree)
          AND (:hasDiscount IS NULL OR (CASE WHEN :hasDiscount = true THEN c.discount > 0 ELSE true END))
          AND (:minPrice IS NULL OR c.price >= :minPrice)
          AND (:maxPrice IS NULL OR c.price <= :maxPrice)
        ORDER BY c.createdAt DESC
        """)
Page<CourseSummaryProjection> searchAndFilterCourses(
        @Param("search") String search,
        @Param("categoryId") Long categoryId,
        @Param("isPublished") Boolean isPublished,
        @Param("isFree") Boolean isFree,
        @Param("hasDiscount") Boolean hasDiscount,
        @Param("minPrice") java.math.BigDecimal minPrice,
        @Param("maxPrice") java.math.BigDecimal maxPrice,
        Pageable pageable);

    /**
     * Tìm course miễn phí
     */
    Page<Course> findByIsFreeTrue(Pageable pageable);

    /**
     * Tìm course có discount
     */
    @Query("SELECT c FROM Course c WHERE c.discount > 0 ORDER BY c.createdAt DESC")
    Page<Course> findCoursesWithDiscount(Pageable pageable);

    /**
     * Tìm course theo khoảng giá
     */
    @Query("SELECT c FROM Course c WHERE c.price BETWEEN :minPrice AND :maxPrice ORDER BY c.price ASC")
    Page<Course> findCoursesByPriceRange(
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            Pageable pageable);

    /**
     * Tìm course mới nhất
     */
    Page<Course> findByIsPublishedTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Đếm khóa học theo category
     */
    Long countByCategoryId(Long categoryId);

    /**
     * Đếm khóa học miễn phí
     */
    Long countByIsFreeTrueAndIsPublishedTrue();

    /**
     * Tìm course được publish
     */
    Page<Course> findByIsPublishedTrue(Pageable pageable);
    
}
