package com.han.service;

import com.han.pojo.Category;
import com.han.pojo.vo.CategoryVO;
import com.han.pojo.vo.NewItemsVO;

import java.util.List;

/**
 * @Author dell
 * @Date 2021/5/2 21:01
 */
public interface CategoryService {

    /**
     * 查询所有一级分类
     * @return
     */
    public List<Category> queryAllRootLevelCat();

    /**
     * 前端传递来的参数, 通过一级分类的id查询子分类的信息
     * @param rootCatId
     * @return
     */
    public List<CategoryVO> getSubCatList(Integer rootCatId);

    /**
     * 查询首页每个一级分类下的6条最新商品数据
     * @param rootCatId
     * @return
     */
    public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId);
}
