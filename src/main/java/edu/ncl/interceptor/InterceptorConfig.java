package edu.ncl.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        InterceptorRegistration ir=registry.addInterceptor(new WebHandlerInterceptor());
        ir.addPathPatterns("/**");
        ir.excludePathPatterns("/login","/js/**","/*.html","/image/**","/css/**");
    }

}

