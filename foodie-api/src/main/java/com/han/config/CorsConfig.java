package com.han.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 解决跨域问题：前端服务器端口是8080, 后端服务器端口是8088。这就存在安全问题
 * @Author dell
 * @Date 2021/4/29 16:55
 */
@Configuration
public class CorsConfig {

    public CorsConfig() {

    }

    @Bean
    public CorsFilter corsFilter() {
        //1、添加cors配置信息
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://shop.hanknight.shop:8080");
        config.addAllowedOrigin("http://center.hanknight.shop:8080");
        config.addAllowedOrigin("http://shop.hanknight.shop");  //后期加入Nginx提前解决
        config.addAllowedOrigin("http://center.hanknight.shop");
        config.addAllowedOrigin("*");

        //设置是否发送cookie信息
        config.setAllowCredentials(true);

        //设置允许请求的方式（GET、POST、PUT、DELETE...）
        config.addAllowedMethod("*");

        //设置允许的header
        config.addAllowedHeader("*");

        //2、为url添加映射路径
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", config);

        //3、返回重新定义好的corsSource
        return new CorsFilter(corsSource);
    }

}
