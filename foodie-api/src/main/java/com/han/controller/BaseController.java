package com.han.controller;


import com.han.pojo.Orders;
import com.han.pojo.Users;
import com.han.pojo.vo.UsersVO;
import com.han.service.center.MyOrdersService;
import com.han.utils.JSONResult;
import com.han.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.util.UUID;

/**
 * 通用化的controller
 * @Author dell
 * @Date 2021/4/27 12:45
 */
@ApiIgnore
@RestController
public class BaseController {

    @Autowired
    private RedisOperator redisOperator;

    public static final String FOODIE_SHOPCART = "shopcart";

    //评价分页每页显示的记录数（默认）
    public static final Integer COMMON_PAGE_SIZE = 10;
    //搜索分页每页显示的记录数（默认）
    public static final Integer PAGE_SIZE = 20;

    //user用户的token前缀, 相当文件夹最终redis中存放的key-value（redis_user_token:user_id, xxx）
    public static final String REDIS_USER_TOKEN = "redis_user_token";

    // 支付中心的调用地址
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";  //produce

    // 微信支付成功 -> 支付中心 -> 天天吃货平台
    //                         |-> 回调通知的url
//    String payReturnUrl = "http://localhost:8088/orders/notifyMerchantOrderPaid";
    String payReturnUrl = "http://api.hanknight.shop:8088/foodie-api/orders/notifyMerchantOrderPaid";

    //用户上传头像的位置(File.separator表示对应该系统的文件分隔符)
    public static final String IMAGE_USER_FACE_LOCATION = File.separator + "workspaces" +
                                                            File.separator +"images" +
                                                            File.separator +"foodie" +
                                                            File.separator +"faces";

    @Autowired
    public MyOrdersService myOrdersService;

    /**
     * 用于验证用户和订单是否有关联, 避免非法用户调用
     * @param userId
     * @param orderId
     * @return
     */
    public JSONResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if(order == null) {
            return JSONResult.errorMsg("订单不存在！");
        }
        return JSONResult.ok(order);
    }

    // 使用UUID生成一个用户信息token存入redis中（注册和登录都要使用）
    public UsersVO conventUsersVO(Users user) {
        // 实现用户的redis会话(trim删除字符串前后的空格)
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TOKEN + ":" + user.getId(), uniqueToken);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
    }
}
