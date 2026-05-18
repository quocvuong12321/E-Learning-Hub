package com.king.lms.e_learning_hub.entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "courses")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course extends BaseEntity {

    @Column(nullable = false, length = 255)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    String thumbnail;

    @Builder.Default
    Double discount = 0.0;

    @Column(precision = 10, scale = 2)
    BigDecimal price;

    @Builder.Default
    @Column(name = "is_free")
    Boolean isFree = false;

    @Column(unique = true, nullable = false, length = 255)
    String slug;

    @Column(name = "is_published", columnDefinition = "BOOLEAN")
    Boolean isPublished;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    CoursesCategory category;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Section> sections;
}