package com.han.service;

import com.han.pojo.OrderStatus;
import com.han.pojo.bo.ShopcartBO;
import com.han.pojo.bo.SubmitOrderBO;
import com.han.pojo.vo.OrderVO;

import java.util.List;

/**
 * 订单接口
 * @Author dell
 * @Date 2021/5/7 17:50
 */
public interface OrderService {

    /**
     * 创建订单
     * @param submitOrderBO
     * @param shopcartList 购物车信息
     */
    public OrderVO createOrder(List<ShopcartBO> shopcartList, SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    public OrderStatus queryOrderStatusInfo(String orderId);

    /**
     * 关闭超时未支付订单
     */
    public void closeOrder();
}
