package com.han.pojo.vo;

import java.util.Date;

/**
 * 用户展示商品评价的VO
 * @Author dell
 * @Date 2021/5/4 15:50
 */
public class ItemCommentVO {

    private Integer commentLevel;
    private String content;
    private String specName;  //规格
    private Date createdTime;
    private String userFace;  //用户头像
    private String nickname;  //用户昵称

    public Integer getCommentLevel() {
        return commentLevel;
    }

    public void setCommentLevel(Integer commentLevel) {
        this.commentLevel = commentLevel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getUserFace() {
        return userFace;
    }

    public void setUserFace(String userFace) {
        this.userFace = userFace;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
