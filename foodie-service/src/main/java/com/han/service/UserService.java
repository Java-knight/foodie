package com.han.service;

import com.han.pojo.Users;
import com.han.pojo.bo.UserBO;

/**
 * @Author dell
 * @Date 2021/4/28 17:41
 */
public interface UserService {

    /**
     * 判断用户名是否存在
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 创建用户
     * @param userBO  接收前端传过来的信息（List、Object...类型）
     * @return
     */
    public Users createUser(UserBO userBO);

    /**
     * 检索用户名和密码是否匹配, 用于登录
     * @param username
     * @param password
     * @return
     */
    public Users queryUserForLogin(String username, String password);
}
