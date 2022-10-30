package com.han.enums;

/**
 * 注意枚举的属性使用“,”分开的
 * @Desc: 性别 枚举
 * @Date 2021/4/29 11:00
 */
public enum Sex {
    woman(0, "女"),
    man(1, "男"),
    secret(2, "保密");

    public final Integer type;
    public final String value;

    Sex(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}


