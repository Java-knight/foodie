package com.han.exception;

import com.han.utils.JSONResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 异常处理类
 * @Author dell
 * @Date 2021/5/12 0:15
 */
@RestControllerAdvice  //SpringBoot统一异常处理
public class CustomExceptionHandler {

    //MaxUploadSizeExceededException: 超过最大文件上传大小(yml中配置了500Kb)异常处理
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public JSONResult handlerMaxUploadFile(MaxUploadSizeExceededException exception) {
        return JSONResult.errorMsg("文件上传大小不能超过500Kb, 请压缩图片或者降低图片质量, 再上传！");
    }

}
