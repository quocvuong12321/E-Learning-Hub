package com.king.lms.e_learning_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.king.lms.e_learning_hub.entity.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findBySectionIdOrderByOrderIndexAsc(Long sectionId);

}