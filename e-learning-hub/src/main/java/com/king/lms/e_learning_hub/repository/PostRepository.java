package com.king.lms.e_learning_hub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.king.lms.e_learning_hub.dto.post.PostSummaryProjection;
import com.king.lms.e_learning_hub.entity.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
            SELECT  p.id as id,
                    p.title as title,
                    p.slug as slug,
                    p.summary as summary,
                    p.thumbnail as thumbnail,
                    p.status as status,
                    p.viewCount as viewCount,
                    c.name as categoryName
            FROM Post p
            LEFT JOIN p.category c
            WHERE (:status IS NULL OR p.status = :status)
            AND (:categoryId IS NULL OR c.id = :categoryId)
            AND (:search IS NULL OR (
                LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR
                LOWER(p.summary) LIKE LOWER(CONCAT('%', :search, '%'))
                ))
            """)
    Page<PostSummaryProjection> searchPostSummaries(@Param("search") String search,
            @Param("categoryId") Long categoryId,
            @Param("status") Post.PostStatus status,
            Pageable pageable);

    Optional<Post> findBySlug(String slug);

    boolean existsBySlug(String slug);

}
