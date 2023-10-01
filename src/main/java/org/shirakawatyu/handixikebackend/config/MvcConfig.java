package org.shirakawatyu.handixikebackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Autowired
    TimeInterceptor timeInterceptor;
    @Autowired
    LoginInterceptor loginInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/api/v2/login/**")
                .excludePathPatterns("/api/gethitokoto")
                .excludePathPatterns("/api/count")
                .excludePathPatterns("/api/week")
                .excludePathPatterns("/api/v2/course/local/**");

        registry.addInterceptor(timeInterceptor)
                .excludePathPatterns("/api/v2/login/loginCheck")
                .excludePathPatterns("/api/gethitokoto")
                .excludePathPatterns("/api/count")
                .excludePathPatterns("/api/week")
                .excludePathPatterns("/api/v2/course/local/**");
    }
}
