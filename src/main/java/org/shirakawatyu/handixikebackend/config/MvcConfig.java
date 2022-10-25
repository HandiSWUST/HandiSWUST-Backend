package org.shirakawatyu.handixikebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        HandlerInterceptor interceptor = new LoginInterceptor();
        registry
                .addInterceptor(interceptor)
                .excludePathPatterns("/api/key")
                .excludePathPatterns("/api/captcha")
                .excludePathPatterns("/api/login")
                .excludePathPatterns("/api/loginCheck")
                .excludePathPatterns("/api/gethitokoto")
                .excludePathPatterns("/api/count");
    }
}
