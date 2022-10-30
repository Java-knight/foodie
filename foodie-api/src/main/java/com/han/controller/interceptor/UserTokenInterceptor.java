package com.han.controller.interceptor;

import com.han.utils.JSONResult;
import com.han.utils.JsonUtils;
import com.han.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 用户拦截器（基于会话Token实现）
 * @Author dell
 * @Date 2021/5/31 15:21
 */
public class UserTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisOperator redisOperator;

    //user用户的token前缀, 相当文件夹最终redis中存放的key-value（redis_user_token:user_id, xxx）
    public static final String REDIS_USER_TOKEN = "redis_user_token";

    /**
     * 拦截请求, 在访问controller调用之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // System.out.println("进入到拦截器, 被拦截...");
        /**
         * false: 请求被拦截, 被驳回, 验证出现问题
         * true: 请求在经过校验以后, 是OK的, 可以通过拦截器
         */

        // 从前端获取传来的参数
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        // 前端为null就返回的是undefined（这第一次if其实是没必要的）

        if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String uniqueToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            // 这里还需要进行判断, 可能用户的token信息被篡改, 从Redis中没有找到
            if(StringUtils.isBlank(uniqueToken)) {

                // System.out.println("请登录...");
                returnErrorResponse(response, JSONResult.errorMsg("请登录..."));
                return false;
            } else {
                // 用户传来的token和redis的token不一样（单点登录的实现）
                if(!uniqueToken.equals(userToken)) {
                    // System.out.println("账号在异地登录...");
                    returnErrorResponse(response, JSONResult.errorMsg("账号在异地登录..."));
                    return false;
                }
            }
        } else {
            // System.out.println("请登录...");
            returnErrorResponse(response, JSONResult.errorMsg("请登录..."));
            return false;
        }

        return true;
    }

    // 将错误信息result, 返回给前端
    public void returnErrorResponse(HttpServletResponse response, JSONResult result) {

        // 输出的流
        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 请求访问controller之后, 渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求访问controller之后, 渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
