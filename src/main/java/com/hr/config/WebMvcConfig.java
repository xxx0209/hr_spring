package com.hr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${uploadPath}")
    private String uploadPath; // 실제 업로드 경로 (끝에 / 포함)

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 외부 경로 -> URL 맵핑
        registry.addResourceHandler("/images/**")   // URL 요청 패턴
                .addResourceLocations(uploadPath);  // 실제 물리 경로
    }
}