package com.king.lms.e_learning_hub.controller;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.king.lms.e_learning_hub.dto.Response.ApiResponse;
import com.king.lms.e_learning_hub.dto.Response.PageResponse;
import com.king.lms.e_learning_hub.dto.post.PostRequest;
import com.king.lms.e_learning_hub.dto.post.PostResponse;
import com.king.lms.e_learning_hub.dto.post.PostSummaryProjection;
import com.king.lms.e_learning_hub.service.PostService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/post")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class PostController {

    PostService postService;

    @PreAuthorize("hasRole('admin')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> createPost(
        @RequestPart("data") PostRequest request,
        @RequestPart(value = "file", required = false)MultipartFile file){

            return ApiResponse.<PostResponse>builder()
            .result(postService.createPost(request, file))
            .build();
    }

    @PreAuthorize("hasRole('admin')")
    @PutMapping(path = "/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> updatePost(
        @RequestPart("data") PostRequest request,
        @RequestPart(value = "file", required = false)MultipartFile file,
        @PathVariable long id){
        
            return ApiResponse.<PostResponse>builder()
            .result(postService.updatePost(id,request, file))
            .build();
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable long id){
        postService.deletePost(id);
        return ApiResponse.<Void>builder().build();
    }
    @GetMapping
    public ApiResponse<PageResponse<PostSummaryProjection>> getPosts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "categoryId", required = false, defaultValue = "0") Long categoryId,
            @RequestParam(value = "status", required = false) String statusString) {
            
        Long finalCategoryId  = categoryId > 0 ? categoryId:null;                
        
        return ApiResponse.<PageResponse<PostSummaryProjection>>builder()
                .result(postService.getPosts(page, size, search, finalCategoryId, statusString))
                .build();
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/image")
    public  ApiResponse<Set<String>> saveImageOfContentPost(@RequestPart List<MultipartFile> files,
                                                            @RequestParam("slug") String slug){

        return ApiResponse.<Set<String>>builder()
                .result(postService.saveImageOfContent(files,slug))
                .build();
    }

    @GetMapping("/{slugOrId}")
    public ApiResponse<PostResponse> postDetail(@PathVariable String slugOrId) {
        try {
            long id = Long.parseLong(slugOrId);
            return ApiResponse.<PostResponse>builder()
                    .result(postService.postDetail(id))
                    .build();
        } catch (NumberFormatException e) {
            return ApiResponse.<PostResponse>builder()
                    .result(postService.postDetail(slugOrId))
                    .build();
        }
    }
}
