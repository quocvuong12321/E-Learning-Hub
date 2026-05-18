package com.king.lms.e_learning_hub.dto.post;

import java.time.LocalDateTime;
import java.util.Set;

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
public class PostResponse {

    long id;
    String title;
    String slug;
    String summary;
    String thumbnail;
    String body;
    Set<String> keywords;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String status;

}
