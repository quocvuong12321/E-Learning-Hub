package com.king.lms.e_learning_hub.dto.course;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CourseRequest {
    
    @NotBlank(message = "Title is required")
    String title;
    
    String description;
    
    String thumbnail;
    
    @Builder.Default
    Double discount = 0.0;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    BigDecimal price;
    
    @Builder.Default
    Boolean isFree = false;
    
    @NotBlank(message = "Slug is required")
    String slug;
    
    Boolean isPublished;
    
    @NotNull(message = "Category ID is required")
    Long categoryId;
}