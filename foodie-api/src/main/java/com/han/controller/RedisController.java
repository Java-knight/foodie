package com.han.controller;

import com.han.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * redis
 * @Author dell
 * @Date 2021/4/27 12:45
 */
@ApiIgnore
@RestController
@RequestMapping("redis")
public class RedisController {

//    @Autowired
//    private RedisTemplate redisTemplate;

    @Autowired
    private RedisOperator redisTemplate;

    @GetMapping("/set")
    public Object set(String key, String value) {

        // redisTemplate.opsForValue().set(key, value);
        redisTemplate.set(key, value);
        return "OK";
    }

    @GetMapping("/get")
    public String get(String key) {
        // return (String) redisTemplate.opsForValue().get(key);
        return redisTemplate.get(key);
    }

    @GetMapping("/delete")
    public Object delete(String key) {
        // redisTemplate.delete(key);
        redisTemplate.del(key);
        return "OK";
    }

    /**
     * 大量key查询
     * @param keys
     * @return
     */
    @GetMapping("/getALot")
    public Object getALot(String... keys) {

        List<String> result = new ArrayList<>();
        for(String k : keys) {
            result.add(redisTemplate.get(k));
        }
        return result;
    }

    /**
     * 批量查询 mget
     * @param keys
     * @return
     */
    @GetMapping("/mget")
    public Object mget(String... keys) {

        List<String> keysList = Arrays.asList(keys);
        return redisTemplate.mget(keysList);
    }

    /**
     * 管道查询
     * @param keys
     * @return
     */
    @GetMapping("/batchGet")
    public Object batchGet(String... keys) {
        List<String> keysList = Arrays.asList(keys);
        return redisTemplate.batchGet(keysList);
    }
}
