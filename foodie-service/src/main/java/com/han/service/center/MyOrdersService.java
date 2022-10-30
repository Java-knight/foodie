package com.han.service.center;

import com.github.pagehelper.PageHelper;
import com.han.pojo.Orders;
import com.han.pojo.vo.OrderStatusCountsVO;
import com.han.utils.PagedGridResult;

/**
 * 用户中心——自己的订单
 * @Author dell
 * @Date 2021/5/12 1:29
 */
public interface MyOrdersService {

    /**
     * 查询我的订单列表, 返回分页的信息
     * @param userId
     * @param orderStatus
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus,
                                         Integer page, Integer pageSize);

    /**
     * 订单状态 ——> 商家发货
     * @param orderId
     */
    public void updateDeliverOrderStatus(String orderId);

    /**
     * 查询我的订单
     * @param userId
     * @param orderId
     * @return
     */
    public Orders queryMyOrder(String userId, String orderId);

    /**
     * 更新订单状态 ——> 确认收货
     * @param orderId
     * @return
     */
    public boolean updateReceiveOrderStatus(String orderId);

    /**
     * 删除订单（逻辑删除）（用户不可见, 但是数据库并没有真正的删除, 只是修改了订单的状态）
     * @param orderId
     * @return
     */
    public boolean deleteOrder(String userId, String orderId);

    /**
     * 查询我的订单状态个数（待发货几个、已付款代发货几个、已发货待收货几个、交易成功几个）
     * @param userId
     */
    public OrderStatusCountsVO getOrderStatusCounts(String userId);

    /**
     * 查询订单动向（展示待付款、已付款代发货、已发货待收货）
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult getOrderTrend(String userId, Integer page, Integer pageSize);
}
