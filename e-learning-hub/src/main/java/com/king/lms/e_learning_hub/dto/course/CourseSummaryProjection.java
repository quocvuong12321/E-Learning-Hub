package com.king.lms.e_learning_hub.dto.course;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CourseSummaryProjection {
    Long getId();
    String getTitle();
    String getThumbnail();
    Double getDiscount();
    BigDecimal getPrice();
    Boolean getIsFree();
    String getSlug();
    Boolean getIsPublished();
    Long getCategoryId();
    String getCategoryName();
    LocalDateTime getCreatedAt();
}
