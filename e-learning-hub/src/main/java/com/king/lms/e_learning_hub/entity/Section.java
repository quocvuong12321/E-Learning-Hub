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
    name = "sections",
    indexes = {@Index(name="idx_course_id",columnList = "course_id")}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Section extends BaseEntity {

    @Column(nullable = false)
    String title;

    @Column(name = "order_index", columnDefinition = "INT DEFAULT 0")
    Integer orderIndex;

    // Quan hệ ManyToOne: Nhiều Sections thuộc về 1 Course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;
    
     // Thêm quan hệ với bảng Lesson
     @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
     List<Lesson> lessons;


}
