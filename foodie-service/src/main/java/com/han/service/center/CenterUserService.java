package com.han.service.center;

import com.han.pojo.Users;
import com.han.pojo.bo.center.CenterUserBO;

/**
 * 用户中心的用户服务
 * @Author dell
 * @Date 2021/5/10 8:15
 */
public interface CenterUserService {

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);

    /**
     * 修改用户信息
     * @param userId
     * @param centerUserBO
     */
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO);

    /**
     * 用户头像更新
     * @param userId
     * @param faceUrl
     * @return
     */
    public Users updateUserFace(String userId, String faceUrl);

}
