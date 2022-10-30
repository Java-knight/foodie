package com.han.service.center;

import com.han.pojo.OrderItems;
import com.han.pojo.Users;
import com.han.pojo.bo.center.OrderItemsCommentBO;
import com.han.utils.PagedGridResult;

import java.util.List;

/**
 * 我的评价
 * @Author dell
 * @Date 2021/5/12 23:16
 */
public interface MyCommentsService {

    /**
     * 根据订单id查询关联的商品
     * @param orderId
     * @return
     */
    public List<OrderItems> queryPendingComment(String orderId);

    /**
     * 保存用户的评论
     * @param userId
     * @param orderId
     * @param commentList
     */
    public void saveComments(String userId, String orderId,
                             List<OrderItemsCommentBO> commentList);

    /**
     * 分页查询我的评价
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize);
}
