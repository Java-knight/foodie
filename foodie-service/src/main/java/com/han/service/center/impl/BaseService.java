package com.han.service.center.impl;

import com.github.pagehelper.PageInfo;
import com.han.utils.PagedGridResult;

import java.util.List;

/**
 * 通用Service
 * @Author dell
 * @Date 2021/5/13 14:45
 */
public class BaseService {

    //进行分页功能（用户中心订单分页）, 这里可以将该方法放在一个BaseService中继承这个类
    public PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        //将数据页进行封装（使用com.han.utils.PagedGridResult进行封装）,
        // 这个插件它是提供了PageInfo, 但是这个类在操作的过程中, 参数会有一些变动,
        // 必须要和前端约定好, 所以我干脆进行了二次开发PagedGridResult, 使用了这个类进行封装
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}
