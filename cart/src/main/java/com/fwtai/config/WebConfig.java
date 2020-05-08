package com.fwtai.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * 拦截器
*/
@Component//注意::::只能使用WebMvcConfigurer,使用WebMvcConfigurationSupport会出现 Could not resolve view with name 'xxx' in servlet with name dispatcherServlet
public class WebConfig implements WebMvcConfigurer{

    /**添加拦截器,把拦截器添加进去并指定排除静态资源的访问路径url,也就是resources/static下有几个目录都要添加进去;exclude 是排除的意思,静态资源配置2*/
    @Override
    public void addInterceptors(final InterceptorRegistry registry){
        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/**").excludePathPatterns(Arrays.asList("/images/**","/js/**","/css/**"));
    }
}