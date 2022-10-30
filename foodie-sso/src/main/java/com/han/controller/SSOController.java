package com.han.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 单点登录
 * @Author dell
 * @Date 2021/6/3 22:48
 */
@Controller
public class SSOController {

    @GetMapping("/hello")
    @ResponseBody
    public Object hello() {
        return "Hello Spring boot";
    }
}
