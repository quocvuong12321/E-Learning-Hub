package com.king.lms.e_learning_hub.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {
    @Bean
    @Primary // Ưu tiên sử dụng bean này nếu có xung đột
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Mẹo ghi điểm CV: Đăng ký module này để xử lý mượt mà các kiểu dữ liệu ngày tháng 
        // như LocalDateTime, LocalDate (rất hay dùng cho created_at, updated_at trong DB)
        mapper.registerModule(new JavaTimeModule()); 
        return mapper;
    }
}
