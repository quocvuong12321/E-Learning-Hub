package com.king.lms.e_learning_hub.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.king.lms.e_learning_hub.dto.Response.PageResponse;
import com.king.lms.e_learning_hub.dto.category.CategoryRequest;
import com.king.lms.e_learning_hub.dto.category.CategoryResponse;
import com.king.lms.e_learning_hub.entity.Category;
import com.king.lms.e_learning_hub.entity.Course;
import com.king.lms.e_learning_hub.entity.CoursesCategory;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.king.lms.e_learning_hub.mapper.CategoryMapper;
import com.king.lms.e_learning_hub.repository.CourseCategoryRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CourseCategoryService {

    CourseCategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    public PageResponse<CategoryResponse> getCategories(int page, int size) {
        //1. Tạo đối tượng Pageable (thường kèm theo Sort để dữ liệu nhất quán)
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("id").ascending());

        Page<CoursesCategory> categories = categoryRepository.findAll(pageable);

        List<CategoryResponse> categoryResponses = categories.map(categoryMapper::toResponse).stream().toList();

        return PageResponse.<CategoryResponse>builder()
                .totalPages(categories.getTotalPages())
                .currentPage(page)
                .pageSize(size)
                .totalElements(categories.getTotalElements())
                .data(categoryResponses)
                .build();
    }

    public Set<CategoryResponse> getCategories(){
        return categoryRepository.findAll().stream().map(categoryMapper::toResponse).collect(Collectors.toSet());
    }

    public CategoryResponse createCategory(CategoryRequest request) {

        if(categoryRepository.existsBySlug(request.getSlug()))
            throw new AppException(ErrorCode.SLUG_EXISTED);

        CoursesCategory category = categoryMapper.toCoursesCategory(request);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        if(categoryRepository.existsBySlug(request.getSlug()))
            throw new AppException(ErrorCode.SLUG_EXISTED);
        CoursesCategory category = categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));

        category.setName(request.getName());
        category.setSlug(request.getSlug());

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        CoursesCategory category = categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));

        categoryRepository.delete(category);
    }

    public CategoryResponse getCategoryById(long id){

        return categoryMapper.toResponse(categoryRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.CATEGORY_NOT_EXIST)));

    }
    
    
}
