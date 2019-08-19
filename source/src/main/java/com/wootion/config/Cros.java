package com.wootion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author: ZhangJiqiang
 * @Description: 跨域配置
 * @Date: Created in 下午5:18 18-5-2
 * @Modified By:
 */
@Configuration
public class Cros extends WebMvcConfigurerAdapter{
    
    /**
     * @Auther: ZhangJiqiang
     * @Description: 跨域配置
     * @Date: 下午5:20 18-5-2
     * @Params: 
     * @return: 
     * 
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH")
                .allowCredentials(true).maxAge(3600);
    }
}
