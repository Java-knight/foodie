package com.han.controller.center;

import com.han.controller.BaseController;
import com.han.pojo.Users;
import com.han.pojo.bo.center.CenterUserBO;
import com.han.pojo.vo.UsersVO;
import com.han.resource.FileUpload;
import com.han.service.center.CenterUserService;
import com.han.utils.CookieUtils;
import com.han.utils.DateUtil;
import com.han.utils.JSONResult;
import com.han.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息controller
 * @Author dell
 * @Date 2021/5/10 8:42
 */
@Api(value = "用户信息接口", tags = {"用户信息相关接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUserController extends BaseController {

    @Autowired
    private CenterUserService centerUserService;
    
    @Autowired
    private FileUpload fileUpload;

    @ApiOperation(value = "用户头像修改", notes = "用户头像修改", httpMethod = "POST")
    @PostMapping("/uploadFace")
    public JSONResult uploadFace(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "file", value = "用户头像", required = true)
            MultipartFile file,
            HttpServletRequest request, HttpServletResponse response) {

        // 需要对文件的后缀名进行校验, 防止黑客攻击.sh .php


        //定义头像保存的地址
//        String fileSpace = IMAGE_USER_FACE_LOCATION;
        String fileSpace = fileUpload.getImageUserFaceLocation();
        // 在路径上为每个用户增加一个userId, 用于区分不同用户上传
        String uploadPathPrefix = File.separator + userId;

        // 开始文件上传
        if(file != null) {

            FileOutputStream fileOutputStream = null;

            try {
                // 获得文件上传的名称（原始文件）
                String fileName = file.getOriginalFilename();

                if (StringUtils.isNotBlank(fileName)) {

                    // 文件重命名  knight-face.png —> ["knight-face", "png"]
                    String[] fileNameArr = fileName.split("\\.");

                    // 获取文件的后缀名
                    String suffix = fileNameArr[fileNameArr.length - 1];

                    if (!suffix.equalsIgnoreCase("png") &&
                            !suffix.equalsIgnoreCase("jpg") &&
                            !suffix.equalsIgnoreCase("jpeg")) {
                        return JSONResult.errorMsg("图片格式不正确！");
                    }

                    // face-{userId}.png
                    // 文件名称重组, 覆盖式上传, 增量式: 额外拼接当前时间
                    String newFileName = "face-" + userId + "." + suffix;

                    // 上传的头像最终保存的位置
                    String finalFacePath = fileSpace + uploadPathPrefix + File.separator + newFileName;

                    //用于提供给web服务的地址
                    uploadPathPrefix += ("/" + newFileName);

                    File outFile = new File(finalFacePath);
                    if(outFile.getParentFile() != null) {
                        //创建文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    //文件输出保存目录
                    fileOutputStream = new FileOutputStream(outFile);
                    InputStream inputStream = file.getInputStream();

                    IOUtils.copy(inputStream, fileOutputStream);
                }


            } catch (IOException e) {
                e.printStackTrace();
            } finally {  //资源关闭
                if(fileOutputStream != null) {
                    try {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            return JSONResult.errorMsg("文件不能为空！");
        }

        // 获取图片服务地址（公共前缀）
        String imageServerUrl = fileUpload.getImageServerUrl();
        // example : http://localhost:8088/foodie/faces + /2104298MGHXNRTHH/face-2104298MGHXNRTHH.png

        // 由于浏览器可能存在缓存情况, 我们需要加上时间戳保证更新后的图片可以及时刷新到浏览器
        String finalUserFaceUrl = imageServerUrl + uploadPathPrefix
                + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);

        // 更新用户头像到数据库
        Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);

//        userResult = setNullProperty(userResult);

        // 后续要改, 增加令牌token, 会整个Redistribution, 分布式会话
        UsersVO usersVO = conventUsersVO(userResult);

        //给cookie中设置值(key-value)并且进行了加密（前端是看不见的）
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);

        return JSONResult.ok();
    }

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("/update")
    public JSONResult update(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        // 判断BindingResult是否保存错误的验证信息, 如果有, 直接return
        if(result.hasErrors()) {
            Map<String, String> errorMap = getError(result);
            return JSONResult.errorMap(errorMap);
        }

        Users userResult = centerUserService.updateUserInfo(userId, centerUserBO);

        // 因为向cookie中传入的是usersVO, 就不需要这行了
//        userResult = setNullProperty(userResult);

        // 后续要改, 增加令牌token, 会整个Redistribution, 分布式会话
        UsersVO usersVO = conventUsersVO(userResult);
        //给cookie中设置值(key-value)并且进行了加密（前端是看不见的）
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);




        return JSONResult.ok();
    }

    //错误处理
    private Map<String, String> getError(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for(FieldError error : errorList) {
            //发生验证错误所对应的某一个属性
            String errorField = error.getField();
            // 验证错误的信息
            String errorMsg = error.getDefaultMessage();

            map.put(errorField, errorMsg);
        }
        return map;
    }


    //将不需要给前端的进行屏蔽
    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }

}
