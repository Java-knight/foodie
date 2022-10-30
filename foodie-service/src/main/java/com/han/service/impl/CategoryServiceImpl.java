package com.han.service.impl;

import com.han.enums.Cats;
import com.han.mapper.CategoryMapper;
import com.han.mapper.CategoryMapperCustom;
import com.han.pojo.Category;
import com.han.pojo.vo.CategoryVO;
import com.han.pojo.vo.NewItemsVO;
import com.han.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品分类处理
 * @Author dell
 * @Date 2021/5/2 21:03
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;  //通用mapper引入

    //自定义mapper的引入
    @Autowired
    private CategoryMapperCustom categoryMapperCustom;

    //商品一级类别查询
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> queryAllRootLevelCat() {

        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", Cats.FIRST.type);

        List<Category> result = categoryMapper.selectByExample(example);

        return result;
    }

    //使用一级分类查询子分类信息(需要使用自定义mapper)
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVO> getSubCatList(Integer rootCatId) {
        return categoryMapperCustom.getSubCatList(rootCatId);
    }

    //查询推荐商品中商品信息
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId) {

        Map<String, Object> map = new HashMap<>();
        map.put("rootCatId", rootCatId);
        return categoryMapperCustom.getSixNewItemsLazy(map);
    }
}
