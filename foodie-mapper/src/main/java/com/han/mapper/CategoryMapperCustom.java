package com.han.mapper;

import com.han.pojo.vo.CategoryVO;
import com.han.pojo.vo.NewItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * CategoryMapper的自定义格式, 因为需要进行商品分类的查询, 比如使用一级分类查询其下面的子分类
 * @Author dell
 * @Date 2021/5/2 21:46
 */
public interface CategoryMapperCustom {

    //根据一级分类的id查询其下面的子分类
    public List<CategoryVO> getSubCatList(Integer rootCatId);

    //根据一级分类的id查询商品列表（分页展示, 6个一组）
    //map中key-value, 比如查询rootCatId = 7  ——>  key-value("rootCatId", 7)
    public List<NewItemsVO> getSixNewItemsLazy(@Param("paramsMap") Map<String, Object> map);
}
