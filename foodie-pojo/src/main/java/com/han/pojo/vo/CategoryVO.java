package com.han.pojo.vo;


import java.util.List;

/**
 * vo：将后端的数据传给前端（view展示层）
 *
 * 二级分类的VO
 * @Author dell
 * @Date 2021/5/2 21:57
 */
public class CategoryVO {

    private Integer id;
    private String name;
    private String type;
    private Integer fatherId;

    //三级分类的VO
    private List<SubCategoryVO> subCatList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getFatherId() {
        return fatherId;
    }

    public void setFatherId(Integer fatherId) {
        this.fatherId = fatherId;
    }

    public List<SubCategoryVO> getSubCatList() {
        return subCatList;
    }

    public void setSubCatList(List<SubCategoryVO> subCatList) {
        this.subCatList = subCatList;
    }
}
