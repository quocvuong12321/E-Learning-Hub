package com.king.lms.e_learning_hub.service;

import com.king.lms.e_learning_hub.dto.Response.PageResponse;
import com.king.lms.e_learning_hub.dto.post.PostRequest;
import com.king.lms.e_learning_hub.dto.post.PostResponse;
import com.king.lms.e_learning_hub.dto.post.PostSummaryProjection;
import com.king.lms.e_learning_hub.entity.Category;
import com.king.lms.e_learning_hub.entity.Keyword;
import com.king.lms.e_learning_hub.entity.Post;
import com.king.lms.e_learning_hub.entity.Post.PostStatus;
import com.king.lms.e_learning_hub.entity.User;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.king.lms.e_learning_hub.mapper.PostMapper;
import com.king.lms.e_learning_hub.repository.CategoryRepository;
import com.king.lms.e_learning_hub.repository.KeywordRepository;
import com.king.lms.e_learning_hub.repository.PostRepository;
import com.king.lms.e_learning_hub.repository.UserRepository;
import com.king.lms.e_learning_hub.util.FileUploadUtils;
import com.king.lms.e_learning_hub.util.JwtUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepository postRepository;
    PostMapper postMapper;
    KeywordRepository keywordRepository;
    JwtUtils jwtUtils;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    FileUploadUtils fileUploadUtils;

    public PostService(PostRepository postRepository,
                       PostMapper postMapper,
                       KeywordRepository keywordRepository,
                       JwtUtils jwtUtils,
                       UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       FileUploadUtils fileUploadUtils) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.keywordRepository = keywordRepository;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.fileUploadUtils = fileUploadUtils;
    }

    @Transactional
    public PostResponse createPost(PostRequest postRequest, MultipartFile file) {

        if (postRepository.existsBySlug(postRequest.getSlug())) {
            throw new AppException(ErrorCode.SLUG_EXISTED);
        }

        String username = jwtUtils.getUserNameByAuthentication();
        User u = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        Category c = categoryRepository.findById(postRequest.getCategory_id()).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));
        Post p = postMapper.toPost(postRequest);

        Set<Keyword> keywordEntities = findOrAddKeyword(postRequest.getKeywords());
        p.setAuthor(u);
        p.setCategory(c);
        p.setViewCount(0);
        p.setStatus(PostStatus.valueOf(postRequest.getPostStatus()));
        p.setKeywords(keywordEntities);
        
        if (!(file == null || file.isEmpty())) {
            String thumbnailUrl = fileUploadUtils.saveImage(file, postRequest.getSlug());
            p.setThumbnail(thumbnailUrl);
        }
        
        return postMapper.toResponse(postRepository.save(p));
    }

    @Transactional
    public PostResponse updatePost(long postId, PostRequest request, MultipartFile file) {
        Post p = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXIST));
        
        // Tìm Category
        Category c = categoryRepository.findById(request.getCategory_id()).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));

        // Cập nhật các trường thông tin cơ bản
        p.setTitle(request.getTitle());
        p.setSlug(request.getSlug());
        p.setSummary(request.getSummary());
        p.setBody(request.getBody());
        p.setStatus(PostStatus.valueOf(request.getPostStatus()));
        p.setCategory(c);

        Set<Keyword> keywords = findOrAddKeyword(request.getKeywords());
        p.setKeywords(keywords);
        
        if (file != null && !file.isEmpty()) {
            if (p.getThumbnail() != null && !p.getThumbnail().isEmpty()) {
                fileUploadUtils.deleteImage(p.getThumbnail());
            }
            String thumbnail = fileUploadUtils.saveImage(file, request.getSlug());
            p.setThumbnail(thumbnail);
        }

        return postMapper.toResponse(postRepository.save(p));
    }

    private Set<Keyword> findOrAddKeyword(Set<String> setString) {
        Set<Keyword> setKeyword = new HashSet<>();
        if (setString != null) {
            for (String kwName : setString) {
                String trimKWName = kwName.trim();
                Keyword kw = keywordRepository.findByName(trimKWName).orElseGet(() -> {
                    Keyword newKW = Keyword.builder()
                            .name(trimKWName)
                            .build();
                    return keywordRepository.save(newKW);
                });
                setKeyword.add(kw);
            }
        }
        return setKeyword;
    }

    public Set<String> saveImageOfContent(List<MultipartFile> files, String slug) {
        return fileUploadUtils.saveImages(files, slug);
    }

    @Transactional
    public void deletePost(long id) {
        Post p = postRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXIST));
        fileUploadUtils.deleteImageFolder(p.getSlug());
        postRepository.delete(p);
    }

    public PageResponse<PostSummaryProjection> getPosts(int page, int size, String search, Long categoryId, String statusString) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        List<String> roles = jwtUtils.getRoleByAuthentication();

        PostStatus status = null;

        if (!roles.contains("ROLE_admin")) {
            status = PostStatus.PUBLISHED;
        } else {
            if (statusString != null && !statusString.trim().isEmpty()) {
                try {
                    status = PostStatus.valueOf(statusString);
                } catch (IllegalArgumentException e) {
                    status = null;
                }
            }
        }

        if (search != null && search.trim().isEmpty()) {
            search = null;
        }

        Page<PostSummaryProjection> result = postRepository.searchPostSummaries(search, categoryId, status, pageable);
        return PageResponse.<PostSummaryProjection>builder()
                .currentPage(page)
                .pageSize(result.getSize())
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .data(result.getContent())
                .build();
    }

    public PostResponse postDetail(String slug) {
        Post p = postRepository.findBySlug(slug).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXIST));

        PostResponse response = postMapper.toResponse(p);
        response.setKeywords(p.getKeywords().stream().map(Keyword::getName).collect(Collectors.toSet()));
        response.setStatus(p.getStatus().name());
        
        return response;
    }

    public PostResponse postDetail(long id) {
        Post p = postRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXIST));

        PostResponse response = postMapper.toResponse(p);
        response.setKeywords(p.getKeywords().stream().map(Keyword::getName).collect(Collectors.toSet()));
        response.setStatus(p.getStatus().name());
        
        return response;
    }
}