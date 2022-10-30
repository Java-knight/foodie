package com.han.mapper;

import com.han.pojo.OrderStatus;
import com.han.pojo.vo.MyOrdersVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单的自定义mapper
 * @Author dell
 * @Date 2021/5/12 1:02
 */
public interface OrdersMapperCustom {

    //查询自己的订单信息(订单的状态, 商品图片等信息)
    public List<MyOrdersVO> queryMyOrders(@Param("paramsMap") Map<String, Object> map);

    // 获取我的订单状态个数（待发货几个、待收货几个、待评价几个）
    public int getMyOrderStatusCounts(@Param("paramsMap") Map<String, Object> map);

    // 查询订单动向信息
    public List<OrderStatus> getMyOrderTrend(@Param("paramsMap") Map<String, Object> map);

}
