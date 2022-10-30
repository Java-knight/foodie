package com.han.enums;

/**
 * 评价等级的枚举类: 1-好评、2-中评、3-差评
 * @Author dell
 * @Date 2021/5/3 19:11
 */
public enum CommentLevel {

    GOOD(1, "好评"),
    NORMAL(2, "中评"),
    BAD(3, "差评");

    public final Integer type;
    public final String value;

    CommentLevel(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
