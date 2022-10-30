package com.han.pojo.vo;

/**
 * 6个最新商品的简单类型(内层)
 * @Author dell
 * @Date 2021/5/3 14:36
 */
public class SimpleItemVO {

    private String itemId;
    private String itemName;
    private String itemUrl;
    private String createdTime;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
