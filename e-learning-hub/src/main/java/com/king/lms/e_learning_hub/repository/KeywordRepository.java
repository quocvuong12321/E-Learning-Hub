package com.king.lms.e_learning_hub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.lms.e_learning_hub.entity.Keyword;

public interface KeywordRepository extends JpaRepository<Keyword,Long>{

    Optional<Keyword> findByName(String name);

}
