package com.han.controller;

import com.han.pojo.bo.ShopcartBO;
import com.han.utils.JSONResult;
import com.han.utils.JsonUtils;
import com.han.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车
 * @Author dell
 * @Date 2021/5/5 21:34
 */
@Api(value = "购物车接口controller", tags = "购物车接口相关api")
@RequestMapping("shopcart")
@RestController
public class ShopcartController extends BaseController {

    @Autowired
    private RedisOperator redisOperator;

    //商品添加购物车（用户id, 商品bo）, 将信息保存到cookie中
    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public JSONResult add(@RequestParam String userId, @RequestBody ShopcartBO shopcartBO,
                          HttpServletRequest request, HttpServletResponse response) {


        if(StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("");
        }

        // 前端用户在登录的情况下, 添加商品到购物车, 会同时在后端同步购物车到Redis缓存
        // 需要判断当前购物车中包含已经存在的商品, 如果已经存在, 需要做一个数量的累加
        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        List<ShopcartBO> shopcartList = null;
        if(StringUtils.isNotBlank(shopcartJson)) {
            // redis中已经有购物车了
            shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);
            //判断购物车中是否存在已有商品, 如果有的化counts累加
            boolean isHaving = false;
            for(ShopcartBO sc : shopcartList) {
                String tmpSpecId = sc.getSpecId();
                if(tmpSpecId.equals(shopcartBO.getSpecId())) {  //购物车中存在新添加的商品
                    sc.setBuyCounts(sc.getBuyCounts() + shopcartBO.getBuyCounts());
                    isHaving = true;
                }
            }
            if(!isHaving) {
                //购物车中没有新添加的商品
                shopcartList.add(shopcartBO);
            }
        } else {
            // redis中没有购物车
            shopcartList = new ArrayList<>();
            // 直接添加到购物车
            shopcartList.add(shopcartBO);
        }

        // 覆盖现有redis中的购物车
        redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartList));

        return JSONResult.ok();
    }

    //购物车删除商品（用户id, 商品bo）, 将信息保存到cookie中
    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品", httpMethod = "POST")
    @PostMapping("/del")
    public JSONResult del(@RequestParam String userId, @RequestParam String itemSpecId,
                          HttpServletRequest request, HttpServletResponse response) {


        if(StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {

            return JSONResult.errorMsg("参数不能为空");
        }

        // 用户在页面删除购物车中的商品数据, 如果此时用户已经登录, 则需要同步伤处后端存储在Redis中购物车的商品
        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        if(StringUtils.isNotBlank(shopcartJson)) {
            // Redis中已经有购物车了
            List<ShopcartBO> shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);

            // 判断购物车中是否存在已有商品, 如果有的话则删除
            for(ShopcartBO sc : shopcartList) {
                String tmpSpecId = sc.getSpecId();
                if(tmpSpecId.equals(itemSpecId)) {
                    shopcartList.remove(sc);
                    break;
                }
            }

            // 覆盖现有redis中的购物车
            redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartList));
        }

        return JSONResult.ok();
    }
}
