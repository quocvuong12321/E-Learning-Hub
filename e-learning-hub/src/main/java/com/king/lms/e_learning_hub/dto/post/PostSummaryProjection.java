package com.king.lms.e_learning_hub.dto.post;

public interface PostSummaryProjection {
    Long getId();
    String getTitle();
    String getSlug();
    String getSummary();
    String getThumbnail();
    String getStatus(); // Tương ứng với PostStatus enum hoặc String
    Integer getViewCount();
    String getCategoryName();
}
