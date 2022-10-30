package com.han.service;

import com.han.pojo.Carousel;

import java.util.List;

/**
 * @Author dell
 * @Date 2021/5/2 16:08
 */
public interface CarouselService {

    /**
     * 查询所有轮播图列表
     * @param isShow
     * @return
     */
    public List<Carousel> queryAll(Integer isShow);
}
