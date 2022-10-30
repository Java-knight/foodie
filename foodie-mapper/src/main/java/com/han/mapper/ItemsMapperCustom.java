package com.han.mapper;

import com.han.pojo.vo.ItemCommentVO;
import com.han.pojo.vo.SearchItemsVO;
import com.han.pojo.vo.ShopcartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 自定义mapper: 商品用户评价
 * @Author dell
 * @Date 2021/5/4 15:41
 */
public interface ItemsMapperCustom {

    //通过商品id和评价等级查询评价
    public List<ItemCommentVO> queryItemComments(@Param("paramsMap") Map<String, Object> map);

    //通过 关键字 搜索商品（keywords）
    public List<SearchItemsVO> searchItems(@Param("paramsMap") Map<String, Object> map);

    //通过 商品的三级分类id 搜索商品（catId）
    public List<SearchItemsVO> searchItemsByThirdCat(@Param("paramsMap") Map<String, Object> map);

    //通过 商品规格id查询购物车商品信息
    public List<ShopcartVO> queryItemsBySpecIds(@Param("paramsList") List specIdsList);

    //减少库存（乐观锁）
    public int decreaseItemSpecStock(@Param("specId") String specId, @Param("pendingCounts") int pendingCounts);
}
