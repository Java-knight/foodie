package com.han.service;

import com.han.pojo.Items;
import com.han.pojo.ItemsImg;
import com.han.pojo.ItemsParam;
import com.han.pojo.ItemsSpec;
import com.han.pojo.vo.CommentLevelCountsVO;
import com.han.pojo.vo.ItemCommentVO;
import com.han.pojo.vo.ShopcartVO;
import com.han.utils.PagedGridResult;

import java.util.List;

/**
 * @Author dell
 * @Date 2021/5/3 16:17
 */
public interface ItemService {

    /**
     * 根据商品id查询商品详情
     * @param itemId
     * @return
     */
    public Items queryItemById(String itemId);

    /**
     * 根据商品id查询商品图片列表
     * @param itemId
     * @return
     */
    public List<ItemsImg> queryItemImgList(String itemId);

    /**
     * 根据商品id查询商品规格信息
     * @param itemId
     * @return
     */
    public List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品id查询商品参数
     * @param itemId
     * @return
     */
    public ItemsParam queryItemParam(String itemId);

    /**
     * 根据商品id查询商品的评价等级数量
     * @param itemId
     */
    public CommentLevelCountsVO queryCommentCounts(String itemId);

    /**
     * 根据 商品id 和 评价等级 查询商品评价（分页）
     * @param itemId
     * @param level
     * @return
     */
    public PagedGridResult queryPagedComments(String itemId, Integer level,
                                              Integer page, Integer pageSize);

    /**
     * 搜索商品功能开发（分页）, keywords是搜索的关键字, sort排序规则（销量从高到低\价格从低到高\默认）
     * @param keywords
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItems(String keywords, String sort,
                                       Integer page, Integer pageSize);

    /**
     * 搜索商品功能开发（分页）, catId是商品三级分类的id进行搜索
     * @param catId
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItemsThirdCat(Integer catId, String sort,
                                               Integer page, Integer pageSize);

    /**
     * 根据规格ids查寻最新购物车中商品数据（用于刷新渲染购物车的商品数据）
     * @param specIds
     * @return
     */
    public List<ShopcartVO> queryItemsBySpecIds(String specIds);

    /**
     * 根据商品规格id获取规格对象的具体信息
     * @param specId
     * @return
     */
    public ItemsSpec queryItemSpecById(String specId);

    /**
     * 根据商品id获得商品 主图片的url
     * @param itemId
     * @return
     */
    public String queryItemMainImgById(String itemId);

    /**
     * 减少库存（注意超卖问题）（分布式锁）
     * @param specId 商品规格id
     * @param buyCounts 减少的数量
     */
    public void decreaseItemSpecStock(String specId, int buyCounts);
}
