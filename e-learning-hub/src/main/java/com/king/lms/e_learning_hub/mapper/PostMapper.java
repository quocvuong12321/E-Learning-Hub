package com.king.lms.e_learning_hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.king.lms.e_learning_hub.dto.post.PostRequest;
import com.king.lms.e_learning_hub.dto.post.PostResponse;
import com.king.lms.e_learning_hub.entity.Post;

@Mapper(componentModel ="Spring")
public interface PostMapper {
    @Mapping(target = "keywords", ignore = true)
    PostResponse toResponse(Post post);

    @Mapping(target = "keywords", ignore = true)
    Post toPost(PostRequest request);
}
