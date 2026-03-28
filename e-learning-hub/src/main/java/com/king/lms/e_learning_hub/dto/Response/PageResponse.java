package com.king.lms.e_learning_hub.dto.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import com.king.lms.e_learning_hub.dto.post.PostSummaryProjection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    int currentPage;
    int totalPages;
    int pageSize;
    long totalElements;
    List<T> data;
}