package com.king.lms.e_learning_hub.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import java.nio.file.Files;
@RestController
@AllArgsConstructor
@RequestMapping("/image")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageController {
    // Đường dẫn gốc nơi bạn lưu ảnh (tương ứng với uploadDir trong code của bạn)
    String uploadDir = "uploads/images";

    @GetMapping("/{slug}/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String slug, @PathVariable String filename) {
        try {
            // 1. Xác định đường dẫn file dựa trên cấu trúc folder: uploadDir/slug/filename
            Path filePath = Paths.get(uploadDir).resolve(slug).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // 2. Kiểm tra file có tồn tại và có thể đọc được không
            if (resource.exists() || resource.isReadable()) {

                // 3. Tự động xác định định dạng ảnh (png, jpg, webp...) để trình duyệt hiển thị đúng
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                // Trả về lỗi 404 nếu không thấy file
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Could not determine file type.");
        }
    }

}

