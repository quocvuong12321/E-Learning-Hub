package com.king.lms.e_learning_hub.util;

import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileUploadUtils {

    String uploadDir;

    public FileUploadUtils(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    /**
     * Lưu một file ảnh vào thư mục theo slug
     *
     * @param file file cần lưu
     * @param slug slug của resource
     * @return đường dẫn file (slug/slug_yyyyMMdd_HHmmss_SSS.extension)
     */
    public String saveImage(MultipartFile file, String slug) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir, slug);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Lấy extension từ tên file gốc
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Sinh tên file: slug + "_" + datetime (millisecond) + extension
            String datetime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String newFilename = slug + "_" + datetime + extension;

            // Copy file vào thư mục
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn tương đối
            return slug + "/" + newFilename;

        } catch (IOException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    /**
     * Lưu nhiều file ảnh cùng lúc
     *
     * @param files danh sách file cần lưu
     * @param slug slug của resource
     * @return Set các đường dẫn file
     */
    public Set<String> saveImages(List<MultipartFile> files, String slug) {
        Set<String> urls = new HashSet<>();
        if (files != null) {
            for (MultipartFile file : files) {
                String url = saveImage(file, slug);
                if (url != null) {
                    urls.add(url);
                }
            }
        }
        return urls;
    }

    /**
     * Xóa một file ảnh
     *
     * @param imagePath đường dẫn file (slug/slug_yyyyMMdd_HHmmss_SSS.extension)
     */
    public void deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }

        try {
            Path filePath = Paths.get(uploadDir, imagePath);
            Files.deleteIfExists(filePath);

            // Xóa thư mục slug nếu rỗng
            Path slugPath = filePath.getParent();
            if (slugPath != null && Files.exists(slugPath)) {
                try {
                    Files.deleteIfExists(slugPath);
                } catch (DirectoryNotEmptyException e) {
                    // Thư mục còn file khác, không xóa
                }
            }

        } catch (IOException e) {
            // Không throw exception vì đây là phụ, không ảnh hưởng business logic
        }
    }

    /**
     * Xóa toàn bộ thư mục ảnh theo slug
     *
     * @param slug slug của resource
     */
    public void deleteImageFolder(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return;
        }
        try {
            Path folderPath = Paths.get(uploadDir, slug);
            if (Files.exists(folderPath)) {
                FileSystemUtils.deleteRecursively(folderPath);
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
}