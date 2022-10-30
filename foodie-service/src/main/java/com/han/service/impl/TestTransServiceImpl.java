package com.han.service.impl;

import com.han.service.StuService;
import com.han.service.TestTransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * 事务测试
 * @Author dell
 * @Date 2021/4/28 12:07
 */
@Service
public class TestTransServiceImpl implements TestTransService {

    @Autowired
    private StuService stuService;

    /**
     * 事务传播 —— Propagation
     *      REQUIRED: 使用当前的事务, 如果当前没有事务, 则自己新建一个事务, 子方法是必须运行在一个事务中的;
     *                如果当前存在事务, 则加入这个事务, 成为一个整体
     *                用途：主要用于增、删、改
     *                举例：领导没饭吃, 我有钱, 我会自己买了自己吃; 领导有的吃, 会分给你一起吃
     *      SUPPORTS: 如果当前有事务, 就使用事务, 如果当前没有事务, 就不使用事务。
     *                用途：主要用于查询
     *                举例：领导没饭池, 我也没饭吃; 领导有饭吃, 我也有饭吃
     *      MANDATORY: 该传播属性强制必须存在一个事务, 如果不存在, 则抛出异常
     *                 举例：领导必须管饭, 不管饭没饭吃, 我就不乐意了, 就不干了（抛出异常）
     *      REQUIRES_NEW: 如果当前有事务, 则挂起该事务, 并且自己创建一个新的事务给自己使用;
     *                    如果当前没有事务, 则同REQUIRES
     *                    举例：领导给饭吃, 我偏不吃, 我自己买了自己吃
     *      NOT_SUPPORTED: 如果当前有事务, 则把事务挂起, 自己不使用事务去运行数据库操作
     *                     如果当前没有事务, 则同SUPPORTED
     *                     举例：领导有饭吃, 分一点给你, 我太忙了, 放一边, 我不吃
     *      NEVER: 如果当前有事务存在, 则抛出异常; 没有事务, 正常工作
     *             举例：领导有饭给你吃, 我不吃, 我热爱工作, 我抛出异常
     *      NESTED: 如果当前有事务（嵌套事务）, 嵌套事务是独立提交或回滚;
     *              如果当前没有事务, 则通REQUIRED
     *              但是如果主事务提交, 则会携带子事务一起提交。
     *              如果主事务, 则子事务会一起回滚
     *              举例：领导决策不对, 老板怪罪, 领导带着小弟一同受罪。小弟出了差错, 领导可以推卸责任
     */

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void testPropagationTrans() {
        stuService.saveParent();

        stuService.saveChildren();
        int a = 1 / 0;
    }
}


