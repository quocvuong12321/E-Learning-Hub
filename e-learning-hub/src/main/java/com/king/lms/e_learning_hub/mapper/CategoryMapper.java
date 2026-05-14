package com.king.lms.e_learning_hub.mapper;


import com.king.lms.e_learning_hub.dto.category.CategoryRequest;
import com.king.lms.e_learning_hub.dto.category.CategoryResponse;
import com.king.lms.e_learning_hub.entity.Category;
import com.king.lms.e_learning_hub.entity.CoursesCategory;

import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface CategoryMapper {

    Category toCategory(CategoryRequest request);

    CategoryResponse toResponse(Category category);


    CoursesCategory toCoursesCategory(CategoryRequest request);

    CategoryResponse toResponse(CoursesCategory category);

}
