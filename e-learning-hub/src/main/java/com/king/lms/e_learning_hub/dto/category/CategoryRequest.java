package com.king.lms.e_learning_hub.dto.category;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Filter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {

    @NotNull(message = "Danh muc khong duoc de trong")
    String name;
    @NotNull(message = "slug khong duoc de trong")
    String slug;

}
