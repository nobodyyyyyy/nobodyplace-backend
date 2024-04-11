package com.nobody.nobodyplace.config;

import com.nobody.nobodyplace.interceptor.JwtTokenInterceptor;
import com.nobody.nobodyplace.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
public class WebConfigurer implements WebMvcConfigurer {

    @Bean
    public LoginInterceptor getLoginInterceptor() {
        return new LoginInterceptor();
    }

    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
//        registry.addInterceptor(getLoginInterceptor()).addPathPatterns("/**").excludePathPatterns("/login");

        // 上面代码不用了
//
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/login", "/home", "/api/coupon/**");

    }
}
