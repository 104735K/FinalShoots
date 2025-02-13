package com.Shoots;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${my.savefolder2}")
    private String SAVE_FOLDER;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /upload/** 요청을 C 드라이브의 c:/upload/ 디렉토리로 매핑
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:"+SAVE_FOLDER+"/");  // 절대 경로
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("https://www.goshoots.site") // 허용할 도메인
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
//                .allowCredentials(true)
//                .allowedHeaders("*");
//    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://www.goshoots.site")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }


}
