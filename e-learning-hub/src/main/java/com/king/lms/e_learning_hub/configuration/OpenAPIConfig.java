package com.king.lms.e_learning_hub.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Tạo một server config với đường dẫn tương đối dựa theo context-path
        Server relativeServer = new Server();
        relativeServer.setUrl("/elearning-hub");
        relativeServer.setDescription("Default Server URL (Relative)");

        return new OpenAPI()
                .servers(List.of(relativeServer));
    }
}