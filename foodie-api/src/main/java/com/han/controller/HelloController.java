package com.han.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * springboot访问测试
 * @Author dell
 * @Date 2021/4/27 12:45
 */
@ApiIgnore  //进行测试的不会加载到api文档中（Swagger2）
@RestController
public class HelloController {

    final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/hello")
    public Object hello() {
        logger.debug("debug: hello~");
        logger.info("info: hello~");
        logger.warn("warn: hello~");
        logger.error("error: hello~");

        return "Hello Spring boot";
    }

    @GetMapping("/setSession")
    public Object setSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("userInfo", "new user");  //设置key-value
        session.setMaxInactiveInterval(3600);  //设置过期时间
        session.getAttribute("userInfo");
        //session.removeAttribute("userInfo");  //删除key-value
        return "ok";
    }
}
