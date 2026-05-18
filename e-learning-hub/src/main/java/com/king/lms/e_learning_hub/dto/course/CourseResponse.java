package com.king.lms.e_learning_hub.dto.course;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.king.lms.e_learning_hub.dto.category.CategoryResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResponse {
    
    Long id;
    
    String title;
    
    String description;
    
    String thumbnail;
    
    Double discount;
    
    BigDecimal price;
    
    Boolean isFree;
    
    String slug;
    
    Boolean isPublished;
    
    CategoryResponse category;
    
    LocalDateTime createdAt;
    
    LocalDateTime updatedAt;
}