package com.han.enums;

/**
 * 注意枚举的属性使用“,”分开的
 * @Desc: 是否 枚举（轮播图是否展示）
 * @Date 2021/4/29 11:00
 */
public enum YesOrNo {
    NO(0, "否"),
    YES(1, "是");

    public final Integer type;
    public final String value;

    YesOrNo(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}


