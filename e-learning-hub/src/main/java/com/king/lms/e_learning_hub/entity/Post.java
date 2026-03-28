package com.king.lms.e_learning_hub.entity;


import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
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
