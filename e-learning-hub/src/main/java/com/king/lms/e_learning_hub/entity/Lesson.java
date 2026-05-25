package com.king.lms.e_learning_hub.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
    name = "lessons",
    indexes = {@Index(name = "idx_section_id", columnList = "section_id")}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lesson extends BaseEntity {

    @Column(nullable = false)
    String title;

    @Column(name = "video_url", columnDefinition = "TEXT")
    String videoUrl;

    @Column(name = "duration_seconds", columnDefinition = "BIGINT DEFAULT 0")
    Long durationSeconds;

    @Column(columnDefinition = "LONGTEXT")
    String content;

    @Column(name = "is_preview", columnDefinition = "BOOLEAN DEFAULT FALSE")
    Boolean isPreview;

    @Column(name = "order_index", columnDefinition = "INT DEFAULT 0")
    Integer orderIndex;

    // Quan hệ ManyToOne: Nhiều Lesson thuộc về 1 Section
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    Section section;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Quiz> quizzes;
}
