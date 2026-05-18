package com.king.lms.e_learning_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.lms.e_learning_hub.entity.CoursesCategory;

public interface CourseCategoryRepository extends JpaRepository<CoursesCategory,Long>{
    boolean existsBySlug(String slug);

}
