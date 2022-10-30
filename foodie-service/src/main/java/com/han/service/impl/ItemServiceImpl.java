package com.han.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.han.enums.CommentLevel;
import com.han.enums.YesOrNo;
import com.han.mapper.*;
import com.han.pojo.*;
import com.han.pojo.vo.CommentLevelCountsVO;
import com.han.pojo.vo.ItemCommentVO;
import com.han.pojo.vo.SearchItemsVO;
import com.han.pojo.vo.ShopcartVO;
import com.han.service.ItemService;
import com.han.utils.DesensitizationUtil;
import com.han.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 商品详情页面处理
 * @Author dell
 * @Date 2021/5/3 16:17
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemsMapper itemsMapper;  //商品信息
    @Autowired
    private ItemsImgMapper itemsImgMapper;  //商品图片
    @Autowired
    private ItemsSpecMapper itemsSpecMapper;  //商品规格
    @Autowired
    private ItemsParamMapper itemsParamMapper;  //商品参数
    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;  //商品评价
    @Autowired
    private ItemsMapperCustom itemsMapperCustom;  //商品评价（自定义mapper）


    //根据商品id查询商品信息
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    //根据商品id查询商品图片
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {

        Example itemsImgExp = new Example(ItemsImg.class);
        Example.Criteria criteria = itemsImgExp.createCriteria();
        criteria.andEqualTo("itemId", itemId);

        return itemsImgMapper.selectByExample(itemsImgExp);
    }

    //根据商品id查询商品规格
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {

        Example itemsSpecExp = new Example(ItemsSpec.class);
        Example.Criteria criteria = itemsSpecExp.createCriteria();
        criteria.andEqualTo("itemId", itemId);

        return itemsSpecMapper.selectByExample(itemsSpecExp);
    }

    //根据商品id查询商品参数
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParam(String itemId) {

        Example itemsParamExp = new Example(ItemsParam.class);
        Example.Criteria criteria = itemsParamExp.createCriteria();
        criteria.andEqualTo("itemId", itemId);

        return itemsParamMapper.selectOneByExample(itemsParamExp);
    }

    //通过商品id查询商品评价等级数量
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentLevelCountsVO queryCommentCounts(String itemId) {

        Integer goodCounts = getCommentCounts(itemId, CommentLevel.GOOD.type);
        Integer normalCounts = getCommentCounts(itemId, CommentLevel.NORMAL.type);
        Integer badCounts = getCommentCounts(itemId, CommentLevel.BAD.type);
        Integer totalCounts = goodCounts + normalCounts + badCounts;

        //将查询出的4个个数封装到vo
        CommentLevelCountsVO countsVO = new CommentLevelCountsVO();
        countsVO.setTotalCounts(totalCounts);
        countsVO.setGoodCounts(goodCounts);
        countsVO.setNormalCounts(normalCounts);
        countsVO.setBadCounts(badCounts);
        return countsVO;
    }

    //输入商品id和需要查询的评价等级返回该商品的该等级个数。比如：查询cook-1001好评 的个数
    @Transactional(propagation = Propagation.SUPPORTS)
    Integer getCommentCounts(String itemId, Integer level) {
        //1、先根据商品的id进行查询
        ItemsComments condition = new ItemsComments();
        condition.setItemId(itemId);

        //2、再根据商品的level进行查询
        if(level != null) {
            condition.setCommentLevel(level);
        }

        return itemsCommentsMapper.selectCount(condition);
    }

    //通过 商品id 和 评价等级 查询商品评价内容（分页）
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("level", level);

        //实现分页, 利用mybatis-pagehelper
        /**
         * 这两个参数是由前端传入的
         * page: 第几页
         * pageSize: 每页显示的条数
         */
        PageHelper.startPage(page, pageSize);

        List<ItemCommentVO> list = itemsMapperCustom.queryItemComments(map);

        //用户信息进行脱敏
        for (ItemCommentVO vo : list) {
            vo.setNickname(DesensitizationUtil.commonDisplay(vo.getNickname()));
        }

        return setterPagedGrid(list, page);
    }

    //进行分页功能（评论 和 搜索）, 这里可以将该方法放在一个BaseService中继承这个类
    private PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        //将数据页进行封装（使用com.han.utils.PagedGridResult进行封装）,
        // 这个插件它是提供了PageInfo, 但是这个类在操作的过程中, 参数会有一些变动,
        // 必须要和前端约定好, 所以我干脆进行了二次开发PagedGridResult, 使用了这个类进行封装
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }

    //搜索商品功能的开发（sort传入的c: 按照销量从高到低排序; p: 按照价格从低到高进行排序）（按照关键字搜索）
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);

        //实现分页, 利用mybatis-pagehelper
        PageHelper.startPage(page, pageSize);

        List<SearchItemsVO> list = itemsMapperCustom.searchItems(map);

        return setterPagedGrid(list, page);
    }

    //搜索商品功能的开发（sort传入的c: 按照销量从高到低排序; p: 按照价格从低到高进行排序）（按照商品的三级分类id搜索）
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItemsThirdCat(Integer catId, String sort, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);

        //实现分页, 利用mybatis-pagehelper
        PageHelper.startPage(page, pageSize);

        List<SearchItemsVO> list = itemsMapperCustom.searchItemsByThirdCat(map);

        return setterPagedGrid(list, page);
    }

    //购物车中的商品展示（通过商品规格id查询出来）(specIds来自与前端的cookie或redis中)
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopcartVO> queryItemsBySpecIds(String specIds) {

        String[] ids = specIds.split(",");
        List<String> specIdsList = new ArrayList<>();
        Collections.addAll(specIdsList, ids);

        return itemsMapperCustom.queryItemsBySpecIds(specIdsList);
    }

    //根据商品规格id查询商品规格信息
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsSpec queryItemSpecById(String specId) {
        return itemsSpecMapper.selectByPrimaryKey(specId);
    }

    //根据商品id查询  主图的url
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemMainImgById(String itemId) {
        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNo.YES.type);
        ItemsImg result = itemsImgMapper.selectOne(itemsImg);
        return result != null ? result.getUrl() : "";
    }

    //注意超卖问题(分布式锁zookeeper、Redis)
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void decreaseItemSpecStock(String specId, int buyCounts) {

        // lockUtil.getLock();  --加锁

        //1、查询库存
        int stock = 10;

        //2、判断库存, 是否能够减少到0以下
        if(stock - buyCounts < 0) {
            //提示库存不够
        }

        // lockUtil.unLock();  --解锁

        int result = itemsMapperCustom.decreaseItemSpecStock(specId, buyCounts);
        if(result != 1) {
            throw new RuntimeException("订单创建失败, 原因: 库存不足");
        }
    }
}
