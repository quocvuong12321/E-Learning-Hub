package com.king.lms.e_learning_hub.controller;


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
import com.king.lms.e_learning_hub.service.CategoryService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    CategoryService categoryService;


    @GetMapping
    public ApiResponse<PageResponse<CategoryResponse>> getCategory(@RequestParam(defaultValue = "20") int size,
                                                                   @RequestParam(defaultValue = "0") int page){
        return ApiResponse.<PageResponse<CategoryResponse>>builder()
                .result(categoryService.getCategories(page,size))
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<Set<CategoryResponse>> getAllCategory(){
        return ApiResponse.<Set<CategoryResponse>>builder()
                .result(categoryService.getCategories())
                .build();
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@Valid  @RequestBody CategoryRequest request){
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.createCategory(request))
                .build();
    }
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request){
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.updateCategory(id,request))
                .build();
    }
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id){

        categoryService.deleteCategory(id);

        return ApiResponse.<Void>builder()
                .build();
    }

}
