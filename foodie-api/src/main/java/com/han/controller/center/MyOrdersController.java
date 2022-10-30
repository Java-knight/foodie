package com.han.controller.center;

import com.han.controller.BaseController;
import com.han.pojo.Orders;
import com.han.pojo.vo.OrderStatusCountsVO;
import com.han.service.center.MyOrdersService;
import com.han.utils.JSONResult;
import com.han.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @Author dell
 * @Date 2021/5/12 1:54
 */
@Api(value = "用户中心我的订单", tags = {"用户中心我的订单相关接口"})
@RequestMapping("myorders")
@RestController
public class MyOrdersController extends BaseController {

    @Autowired
    private MyOrdersService myOrdersService;

    @ApiOperation(value = "查询订单动向", notes = "查询订单动向", httpMethod = "POST")
    @PostMapping("/trend")
    public JSONResult trend(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "page", value = "查询下一页是第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if(StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg(null);
        }
        if(page == null) {
            page = 1;
        }
        if(pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid = myOrdersService.getOrderTrend(userId, page, pageSize);

        return JSONResult.ok(grid);
    }


    @ApiOperation(value = "获得订单状态数概况", notes = "获得订单状态数概况", httpMethod = "POST")
    @PostMapping("/statusCounts")
    public JSONResult statusCounts(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) {

        if(StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg(null);
        }

        OrderStatusCountsVO result = myOrdersService.getOrderStatusCounts(userId);

        return JSONResult.ok(result);
    }

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "POST")
    @PostMapping("/query")
    public JSONResult query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderStatus", value = "订单状态", required = false)
            @RequestParam Integer orderStatus,
            @ApiParam(name = "page", value = "查询下一页是第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if(StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg(null);
        }

        if(page == null) {
            page = 1;
        }
        if(pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid = myOrdersService.queryMyOrders(userId, orderStatus, page, pageSize);

        return JSONResult.ok(grid);
    }

    // 商家发货没有后端, 所以这个接口仅仅只是用于模拟
    @ApiOperation(value = "商家发货", notes = "商家发货", httpMethod = "GET")
    @GetMapping("/deliver")
    public JSONResult deliver(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) {
        if(StringUtils.isBlank(orderId)) {
            return JSONResult.errorMsg("订单ID不能为空");
        }

        myOrdersService.updateDeliverOrderStatus(orderId);
        return JSONResult.ok();
    }

    @ApiOperation(value = "用户确认收货", notes = "用户确认收货", httpMethod = "POST")
    @PostMapping("/confirmReceive")
    public JSONResult confirmReceive(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) {

        JSONResult checkResult = checkUserOrder(userId, orderId);
        if(checkResult.getStatus() != HttpStatus.OK.value()) {  //判断是否有问题
            return checkResult;
        }

        boolean res = myOrdersService.updateReceiveOrderStatus(orderId);
        if(!res) {
            return JSONResult.errorMsg("订单确认收货失败！");
        }

        return JSONResult.ok();
    }

    // 这个和用户确认收货 的代码前面是一样的, 但是不建议放在一起, 耦合度太高, 后期添加新功能不好做
    @ApiOperation(value = "用户删除订单", notes = "用户删除订单", httpMethod = "POST")
    @PostMapping("/delete")
    public JSONResult delete(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) {

        JSONResult checkResult = checkUserOrder(userId, orderId);
        if(checkResult.getStatus() != HttpStatus.OK.value()) {  //判断是否有问题
            return checkResult;
        }

        boolean res = myOrdersService.deleteOrder(userId, orderId);
        if(!res) {
            return JSONResult.errorMsg("订单删除失败！");
        }

        return JSONResult.ok();
    }

    /**
     * 用于验证用户和订单是否有关联, 避免非法用户调用
     * @param userId
     * @param orderId
     * @return
     */
//    private JSONResult checkUserOrder(String userId, String orderId) {
//        Orders order = myOrdersService.queryMyOrder(userId, orderId);
//        if(order == null) {
//            return JSONResult.errorMsg("订单不存在！");
//        }
//        return JSONResult.ok();
//    }
}
