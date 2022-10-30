package com.han.config;

import com.han.service.OrderService;
import com.han.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单的定时任务: 超时未支付直接关闭
 * @Author dell
 * @Date 2021/5/10 0:09
 */
@Component
public class OrderJob {

    @Autowired
    private OrderService orderService;

    //自动关闭超时订单(每小时执行一次)
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void autoCloseOrder() {
        orderService.closeOrder();
        System.out.println("执行定时任务, 当前时间为: " +
                DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN));

    }


}
