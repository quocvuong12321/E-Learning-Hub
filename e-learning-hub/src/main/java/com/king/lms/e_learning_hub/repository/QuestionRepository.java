package com.king.lms.e_learning_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.lms.e_learning_hub.entity.Question;

public interface QuestionRepository extends JpaRepository<Question,Long>{
    
}
