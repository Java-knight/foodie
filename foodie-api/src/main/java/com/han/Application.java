package com.han;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author dell
 * @Date 2021/4/27 12:43
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
//扫描mybatis通用mapper所在的包
@MapperScan(basePackages = "com.han.mapper")
//扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.han", "org.n3r.idworker"})
//开启定时任务
@EnableScheduling
//开启使用redis作为spring-session
@EnableRedisHttpSession
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
