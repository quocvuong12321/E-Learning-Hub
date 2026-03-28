package com.king.lms.e_learning_hub.dto.category;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {

    Long id;
    String name;
    String slug;

}
