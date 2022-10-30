package com.han.enums;

/**
 * 商品分类
 * @Author dell
 * @Date 2021/5/2 21:17
 */
public enum Cats {
    FIRST(1, "一级商品类别"),
    SECOND(2, "二级商品类别"),
    THIRD(3, "三级商品类别");

    public final Integer type;
    public final String value;

    Cats(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
