package com.han.service.impl;

import com.han.enums.OrderStatusEnum;
import com.han.enums.YesOrNo;
import com.han.mapper.OrderItemsMapper;
import com.han.mapper.OrderStatusMapper;
import com.han.mapper.OrdersMapper;
import com.han.pojo.*;
import com.han.pojo.bo.ShopcartBO;
import com.han.pojo.bo.SubmitOrderBO;
import com.han.pojo.vo.MerchantOrdersVO;
import com.han.pojo.vo.OrderVO;
import com.han.service.AddressService;
import com.han.service.ItemService;
import com.han.service.OrderService;
import com.han.utils.DateUtil;
import com.han.utils.RedisOperator;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author dell
 * @Date 2021/5/7 17:51
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;  //订单
    @Autowired
    private OrderItemsMapper orderItemsMapper;  //子订单
    @Autowired
    private OrderStatusMapper orderStatusMapper;  //订单状态
    @Autowired
    private AddressService addressService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private Sid sid;

    //创建订单表(一个订单表中有多个子订单)
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public OrderVO createOrder(List<ShopcartBO> shopcartList, SubmitOrderBO submitOrderBO) {
        String userId = submitOrderBO.getUserId();
        String addressId = submitOrderBO.getAddressId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        Integer payMethod = submitOrderBO.getPayMethod();
        String leftMsg = submitOrderBO.getLeftMsg();  //留言

        //包邮费用设置为0
        Integer postAmount = 0;

        String orderId = sid.nextShort();  //订单id

        UserAddress address = addressService.queryUserAddress(userId, addressId);

        //1、新订单数保存
        Orders newOrder = new Orders();
        newOrder.setId(orderId);
        newOrder.setUserId(submitOrderBO.getUserId());
        newOrder.setReceiverName(address.getReceiver());
        newOrder.setReceiverMobile(address.getMobile());
        newOrder.setReceiverAddress(address.getProvince() + " "
                                    + address.getCity() + " "
                                    + address.getDistrict() + " "
                                    + address.getDetail());

        newOrder.setPostAmount(postAmount);

        newOrder.setPayMethod(payMethod);
        newOrder.setLeftMsg(leftMsg);
        newOrder.setIsComment(YesOrNo.NO.type);  //是否评价（创建的时候肯定没有评价）
        newOrder.setIsDelete(YesOrNo.NO.type);  //是否删除
        newOrder.setCreatedTime(new Date());
        newOrder.setUpdatedTime(new Date());

        //2、循环根据itemSpecIds保存订单商品信息表
        String[] itemSpecIdArr = itemSpecIds.split(",");
        Integer totalAmount = 0;  //商品价格累计
        Integer realPayAmount = 0;  //实际支付价格

        List<ShopcartBO> toBeRemovedShopcatdList = new ArrayList<>();

        for(String itemSpecId : itemSpecIdArr) {

            ShopcartBO cartItem = getBuyCountsFromShopcart(shopcartList, itemSpecId);
            // 整合Redis后, 商品购买的数量重新从Redis的购物车中获取
            int buyCounts = cartItem.getBuyCounts();  //购物车中该规格商品的数量
            toBeRemovedShopcatdList.add(cartItem);

            //2.1 根据规格id, 查询规格的具体信息, 主要获取价格
            ItemsSpec itemsSpec = itemService.queryItemSpecById(itemSpecId);
            totalAmount += itemsSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemsSpec.getPriceDiscount() * buyCounts;

            //2.2 根据商品id, 获取商品信息以及商品图片
            String itemId = itemsSpec.getItemId();
            Items items = itemService.queryItemById(itemId);
            String mainImgUrl = itemService.queryItemMainImgById(itemId);

            //2.3 循环保存子订单数据到数据库
            String subOrderId = sid.nextShort();  //子订单id
            OrderItems subOrderItem = new OrderItems();
            subOrderItem.setId(subOrderId);
            subOrderItem.setOrderId(orderId);
            subOrderItem.setItemId(itemId);
            subOrderItem.setItemName(items.getItemName());
            subOrderItem.setItemImg(mainImgUrl);
            subOrderItem.setBuyCounts(buyCounts);
            subOrderItem.setItemSpecId(itemSpecId);
            subOrderItem.setItemSpecName(itemsSpec.getName());
            subOrderItem.setPrice(itemsSpec.getPriceDiscount());
            orderItemsMapper.insert(subOrderItem);

            //2.4 在用户提交订单后, 规格表 中需要扣除库存
            itemService.decreaseItemSpecStock(itemSpecId, buyCounts);
        }

        newOrder.setTotalAmount(totalAmount);  //总价格
        newOrder.setRealPayAmount(realPayAmount);  //优惠后的价格
        ordersMapper.insert(newOrder);

        //3、保存订单状态表
        OrderStatus waitPayOrderStatus = new OrderStatus();
        waitPayOrderStatus.setOrderId(orderId);
        waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitPayOrderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderStatus);

        //4、构建商户订单, 用于传给支付中心
        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setAmount(realPayAmount + postAmount);
        merchantOrdersVO.setPayMethod(payMethod);
        //returnUrl在controller里面设置

        //5、构建自定义订单vo
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrdersVO(merchantOrdersVO);
        orderVO.setToBeRemovedShopcatdList(toBeRemovedShopcatdList);
        return orderVO;
    }

    /**
     * 获取redis购物车信息每个规格商品的购买数量, 目的：获取count
     * @param shopcartList
     * @param specId
     * @return
     */
    private ShopcartBO getBuyCountsFromShopcart(List<ShopcartBO> shopcartList, String specId) {
        for(ShopcartBO cart : shopcartList) {
            if(cart.getSpecId().equals(specId)) {
                return cart;
            }
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatus queryOrderStatusInfo(String orderId) {
        return orderStatusMapper.selectByPrimaryKey(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void closeOrder() {

        // 查询所有未付款订单, 判断时间是否超过（1天）, 超时则关闭订单
        OrderStatus queryOrder = new OrderStatus();
        queryOrder.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);

        List<OrderStatus> list = orderStatusMapper.select(queryOrder);
        for(OrderStatus os : list) {
            //获得订单创建时间
            Date createTime = os.getCreatedTime();
            // 和当前时间进行对比
            int days = DateUtil.daysBetween(createTime, new Date());
            if(days >= 1) {
                //超过1天, 关闭订单
                doCloseOrder(os.getOrderId());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    void doCloseOrder(String orderId) {
        OrderStatus close = new OrderStatus();
        close.setOrderId(orderId);
        close.setOrderStatus(OrderStatusEnum.CLOSE.type);
        close.setCloseTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(close);
    }
}
