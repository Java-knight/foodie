package com.han.mapper;

import com.han.pojo.vo.MyCommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author dell
 * @Date 2021/5/13 2:22
 */
public interface ItemsCommentsMapperCustom {

    // 保存用户评论
    public void saveComments(Map<String, Object> map);

    // 查询我的评价
    public List<MyCommentVO> queryMyComments(@Param("paramsMap") Map<String, Object> map);
}
