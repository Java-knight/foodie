package com.han.controller;

import com.han.pojo.Users;
import com.han.pojo.bo.ShopcartBO;
import com.han.pojo.bo.UserBO;
import com.han.pojo.vo.UsersVO;
import com.han.service.UserService;
import com.han.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 注册登录的controller
 * @Author dell
 * @Date 2021/4/28 17:50
 */
@Api(value = "注册登录", tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisOperator redisOperator;

    //判断用户名是否存在(查找)
    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public JSONResult usernameIsExist(@RequestParam String username) {
        //1、判断用户名不能为空
        if(StringUtils.isBlank(username)) {
            return JSONResult.errorMsg("用户名不能为空");
        }

        //2、查找注册的用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return JSONResult.errorMsg("用户名已经存在");
        }

        //3、请求成功, 用户名没有重复
        return JSONResult.ok();
    }

    //用户注册（添加）
    @ApiOperation(value = "用户名注册", notes = "用户名注册", httpMethod = "POST")
    @PostMapping("/regist")
    public JSONResult regist(@RequestBody UserBO userBO,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPwd = userBO.getConfirmPassword();

        //0、判断用户名和密码必须不为空
        if(StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(confirmPwd)) {
            return JSONResult.errorMsg("用户名或密码不能为空");
        }

        //1、查询用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return JSONResult.errorMsg("用户名已经存在");
        }

        //2、密码长度不能少于6位
        if(password.length() < 6) {
            return JSONResult.errorMsg("密码长度不能小于6");
        }

        //3、判断两次密码是否一致
        if (!password.equals(confirmPwd)) {
            return JSONResult.errorMsg("两次密码输入不一致");
        }

        //4、实现注册
        Users userResult = userService.createUser(userBO);

        //用户有些信息不需要提交给前端（设置为null）
        userResult = setNullProperty(userResult);

        // 生成用户token, 存入Redis会话
        UsersVO usersVO = conventUsersVO(userResult);

        //给cookie中设置值(key-value)并且进行了加密（前端是看不见的）
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);



        // 同步购物车数据
        syncShopcartData(userResult.getId(), request, response);

        return JSONResult.ok();
    }

    //用户登录（查询）
    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public JSONResult login(@RequestBody UserBO userBO,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        String username = userBO.getUsername();
        String password = userBO.getPassword();

        //0、判断用户名和密码必须不为空
        if(StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            return JSONResult.errorMsg("用户名或密码不能为空");
        }

        //1、实现登录(密码需要MD5加密)
        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

        //判断是否查询到
        if(userResult == null) {
            return JSONResult.errorMsg("用户名或密码错误");
        }

        //用户有些信息不需要提交给前端（设置为null）
        userResult = setNullProperty(userResult);

        // 生成用户token, 存入Redis会话
        UsersVO usersVO = conventUsersVO(userResult);

        //给cookie中设置值(key-value)并且进行了加密（前端是看不见的）
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);

        // 同步购物车数据
        syncShopcartData(userResult.getId(), request, response);

        return JSONResult.ok(userResult);
    }

    /**
     * 注册登录成功后, 同步cookie和redis中购物车中存储的数据
     */
    private void syncShopcartData(String userId, HttpServletRequest request, HttpServletResponse response) {
        /**
         * 1、Redis中无数据, 如果cookie中的购物车为空, 那么这个时候不做任何处理
         *                  如果cookie中的购物车不为空, 此时直接放入Redis中
         * 2、Redis中有数据, 如果cookie中的购物车为空, 那么直接把Redis的购物车覆盖本地cookie
         *                  如果cookie中的某个商品在Redis中存在, 则以cookie的为主,
         *                      删除Redis中的, 把cookie中的商品直接覆盖redis中（参考京东）
         *                  还有一种, 就是把redis中的商品和cookie做一个累加(但是这一般非用户本意)
         *                  还有就是改变设计思路, 用户一旦要把商品加入购物车就必须进行登录（淘宝做法）
         * 3、同步到redis中去了以后, 覆盖本地cookie购物车的数据, 保证本地购物车的数据是同步最新的
         */

        // 从Redis中获取购物车
        String shopcartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);

        // 从cookie中获取购物车
        String shopcartStrCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);

        if(StringUtils.isBlank(shopcartJsonRedis)) {
            //redis为空, 直接将cookie中的数据放入redis
            if(StringUtils.isNotBlank(shopcartStrCookie)) {
                //cookie不为空
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopcartStrCookie);
            } else {
                //cookie为空, 不做任何处理
            }
        } else {
            //redis不为空, cookie不为空, 将数据以cookie中为主, 同一商品覆盖redis中的数据（京东的做法）
            if(StringUtils.isNotBlank(shopcartStrCookie)) {

                /**
                 * (1)已经存在的商品, 把cookie中对应的数量, 覆盖redis（参考京东）
                 * (2)该项商品标记为待删除, 统一放入一个待删除的List
                 * (3)从cookie中清理所有的待删除list
                 * (4)何必跟redis和cookie中的数据
                 * (5)更新到redis和cookie中
                 */
                List<ShopcartBO> shopcartListRedis = JsonUtils.jsonToList(shopcartJsonRedis, ShopcartBO.class);
                List<ShopcartBO> shopcartListCookie = JsonUtils.jsonToList(shopcartStrCookie, ShopcartBO.class);

                // 定义一个待删除的list
                List<ShopcartBO> pendingDeleteList = new ArrayList<>();

                //以redis为主
                for(ShopcartBO redisShopcart : shopcartListRedis) {
                    String redisSpecId = redisShopcart.getSpecId();

                    for(ShopcartBO cookieShopcart : shopcartListCookie) {
                        String cookieSpecId = cookieShopcart.getSpecId();

                        // 相同的商品, 用cookie的覆盖redis
                        if(redisSpecId.equals(cookieSpecId)) {
                            // 覆盖购买数量, 不累加, 参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            // 把cookieShopcart放入待删除列表, 用于最后的删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }
                    }
                }

                // 从现有cookie中删除对应的覆盖过的商品数据
                shopcartListCookie.removeAll(pendingDeleteList);

                // 合并两个list
                shopcartListRedis.addAll(shopcartListCookie);
                // 更新redis和cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartListRedis), true);
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartListRedis));
            } else {
                //redis不为空, cookie为空, 直接将redis的值覆盖cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopcartJsonRedis, true);
            }
        }
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }

    //用户退出登录
    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public JSONResult logout(@RequestParam String userId,
                             HttpServletRequest request,
                             HttpServletResponse response) {

        //清除用户相关的cookie（用户token和用户信息存放在一起）
        CookieUtils.deleteCookie(request, response, "user");

        // 用户退出登录, 清除redis中user的会话信息（token）
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        //分布式会话中需要清除用户数据(清空的是cookie中的购物车)
        CookieUtils.deleteCookie(request, response, FOODIE_SHOPCART);

        return JSONResult.ok();
    }


}
