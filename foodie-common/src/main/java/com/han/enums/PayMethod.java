package com.han.enums;

/**
 * 支付方式的枚举
 * @Author dell
 * @Date 2021/5/7 17:45
 */
public enum PayMethod {

    WEIXIN(1, "微信"),
    ALIPAY(2, "支付宝");

    public final Integer type;
    public final String value;

    PayMethod(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
