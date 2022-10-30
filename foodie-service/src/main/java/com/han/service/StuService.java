package com.han.service;

import com.han.pojo.Stu;

/**
 * 测试
 * @Author dell
 * @Date 2021/4/28 10:53
 */
public interface StuService {

    public Stu getStuInfo(int id);

    public void saveStu();

    public void updateStu(int id);

    public void deleteStu(int id);

    public void saveParent();

    public void saveChildren();
}
