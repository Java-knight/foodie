package com.han.controller;

import com.han.pojo.Items;
import com.han.pojo.ItemsImg;
import com.han.pojo.ItemsParam;
import com.han.pojo.ItemsSpec;
import com.han.pojo.vo.CommentLevelCountsVO;
import com.han.pojo.vo.ItemInfoVO;
import com.han.pojo.vo.ShopcartVO;
import com.han.service.ItemService;
import com.han.utils.JSONResult;
import com.han.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品信息展示
 * @Author dell
 * @Date 2021/5/3 16:50
 */
@Api(value = "商品接口", tags = {"商品信息展示的相关接口"})
@RequestMapping("items")
@RestController
public class ItemsController extends BaseController {

    @Autowired
    private ItemService itemService;

    //通过商品id查询商品详情, 返回的4个数据信息, 需要用一个vo类进行包装
    @ApiOperation(value = "查询商品详情", notes = "查询商品详情", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public JSONResult info(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @PathVariable String itemId) {
        if(StringUtils.isBlank(itemId)) {
            return JSONResult.errorMsg(null);  //swagger2会自动给提示
        }

        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemImgList = itemService.queryItemImgList(itemId);
        List<ItemsSpec> itemSpecList = itemService.queryItemSpecList(itemId);
        ItemsParam itemParam = itemService.queryItemParam(itemId);

        //使用item信息的vo封装一层
        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(item);
        itemInfoVO.setItemImgList(itemImgList);
        itemInfoVO.setItemSpecList(itemSpecList);
        itemInfoVO.setItemParams(itemParam);

        return JSONResult.ok(itemInfoVO);
    }


    //通过商品id查询商品详情, 返回的4个数据信息, 需要用一个vo类进行包装
    @ApiOperation(value = "查询商品评价等级", notes = "查询商品评价等级", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public JSONResult commentLevel(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId) {
        if(StringUtils.isBlank(itemId)) {
            return JSONResult.errorMsg(null);  //swagger2会自动给提示
        }

        CommentLevelCountsVO countsVO = itemService.queryCommentCounts(itemId);

        return JSONResult.ok(countsVO);
    }


    //通过商品id查询商品详情, 返回的4个数据信息, 需要用一个vo类进行包装
    @ApiOperation(value = "查询商品评价", notes = "查询商品评价", httpMethod = "GET")
    @GetMapping("/comments")
    public JSONResult comments(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId,
            @ApiParam(name = "level", value = "商品评价等级", required = false)
            @RequestParam Integer level,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "每页显示的记录数", required = false)
            @RequestParam Integer pageSize) {
        if(StringUtils.isBlank(itemId)) {
            return JSONResult.errorMsg(null);  //swagger2会自动给提示
        }

        if(page == null) {
            page = 1;
        }
        if(pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;  //COMMON_PAGE_SIZE来自父类
        }

        PagedGridResult grid = itemService.queryPagedComments(itemId, level, page, pageSize);

        return JSONResult.ok(grid);
    }

    //通过关键字搜索商品, 按照不同规则进行排序展示
    @ApiOperation(value = "通过关键字搜索商品列表", notes = "通过关键字搜索商品列表", httpMethod = "GET")
    @GetMapping("/search")
    public JSONResult search(
            @ApiParam(name = "keywords", value = "关键字", required = true)
            @RequestParam String keywords,
            @ApiParam(name = "sort", value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "每页显示的记录数", required = false)
            @RequestParam Integer pageSize) {
        if(StringUtils.isBlank(keywords)) {
            return JSONResult.errorMsg(null);  //swagger2会自动给提示
        }

        if(page == null) {
            page = 1;
        }
        if(pageSize == null) {
            pageSize = PAGE_SIZE;  //PAGE_SIZE来自父类
        }

        PagedGridResult grid = itemService.searchItems(keywords, sort, page, pageSize);

        return JSONResult.ok(grid);
    }

    //通过关键字搜索商品, 按照不同规则进行排序展示
    @ApiOperation(value = "通过分类id搜索商品列表", notes = "通过分类id搜索商品列表", httpMethod = "GET")
    @GetMapping("/catItems")
    public JSONResult catItems(
            @ApiParam(name = "catId", value = "商品三级分类的id", required = true)
            @RequestParam Integer catId,
            @ApiParam(name = "sort", value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "每页显示的记录数", required = false)
            @RequestParam Integer pageSize) {
        if(catId == null) {
            return JSONResult.errorMsg(null);  //swagger2会自动给提示
        }

        if(page == null) {
            page = 1;
        }
        if(pageSize == null) {
            pageSize = PAGE_SIZE;  //PAGE_SIZE来自父类
        }

        PagedGridResult grid = itemService.searchItemsThirdCat(catId, sort, page, pageSize);

        return JSONResult.ok(grid);
    }

    //用于用户长时间未登录网站, 刷新购物车中的数据（主要是商品价格）, 类似之前京东淘宝
    @ApiOperation(value = "根据商品规格ids查询最新的商品数据", notes = "根据商品规格ids查询最新的商品数据", httpMethod = "GET")
    @GetMapping("/refresh")
    public JSONResult refresh(
            @ApiParam(name = "itemSpecIds", value = "拼接的规格ids", required = true, example = "1001,1003,1005")
            @RequestParam String itemSpecIds) {
        if(StringUtils.isBlank(itemSpecIds)) {
            return JSONResult.ok();
        }

        List<ShopcartVO> list = itemService.queryItemsBySpecIds(itemSpecIds);

        return JSONResult.ok(list);
    }
}
