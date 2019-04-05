package com.sora.crawler.crawler.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.sora.crawler.crawler.interceptor.FileUploadInterceptor;

@SuppressWarnings("deprecation")
@Configuration
public class FileInterceptorConfig implements WebMvcConfigurer{
 
    @Autowired
    private FileUploadInterceptor fileUploadInterceptor;
    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
 
        registry.addInterceptor(fileUploadInterceptor);
    }
}