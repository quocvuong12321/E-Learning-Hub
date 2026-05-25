package com.king.lms.e_learning_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.lms.e_learning_hub.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz,Long>{ 
    
    List<Quiz> findByLessonId(Long lessionId);
}
