package com.han.pojo.vo;

import com.han.pojo.Items;
import com.han.pojo.ItemsImg;
import com.han.pojo.ItemsParam;
import com.han.pojo.ItemsSpec;

import java.util.List;

/**
 * 商品详情vo
 *    商品信息查询到了商品信息、商品图片、商品规格、商品参数, 需要返回给前端, 需要用这个vo来封装一下
 * @Author dell
 * @Date 2021/5/3 17:00
 */
public class ItemInfoVO {

    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;

    public Items getItem() {
        return item;
    }

    public void setItem(Items item) {
        this.item = item;
    }

    public List<ItemsImg> getItemImgList() {
        return itemImgList;
    }

    public void setItemImgList(List<ItemsImg> itemImgList) {
        this.itemImgList = itemImgList;
    }

    public List<ItemsSpec> getItemSpecList() {
        return itemSpecList;
    }

    public void setItemSpecList(List<ItemsSpec> itemSpecList) {
        this.itemSpecList = itemSpecList;
    }

    public ItemsParam getItemParams() {
        return itemParams;
    }

    public void setItemParams(ItemsParam itemParams) {
        this.itemParams = itemParams;
    }
}
