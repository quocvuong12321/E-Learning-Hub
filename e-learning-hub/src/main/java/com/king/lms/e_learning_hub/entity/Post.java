package com.king.lms.e_learning_hub.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    User author;

    @Column(nullable = false, length = 255)
    String title;

    @Column(unique = true, nullable = false, length = 255)
    String slug;

    @Column(length = 255)
    String summary;

    String thumbnail;

    @Column(columnDefinition = "TEXT")
    String body;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PUBLIC', 'HIDDEN') DEFAULT 'HIDDEN'")
    PostStatus status;

    @Column(name = "view_count")
    Integer viewCount = 0;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "post_keywords",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    Set<Keyword> keywords;


    // Định nghĩa Enum cho trạng thái bài viết
    public enum PostStatus {
        PUBLIC, HIDDEN
    }


}
