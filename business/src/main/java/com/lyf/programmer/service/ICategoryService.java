package com.lyf.programmer.service;

import com.lyf.programmer.dto.CategoryDTO;
import com.lyf.programmer.dto.PageDTO;
import com.lyf.programmer.dto.ResponseDTO;

import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-11-29 11:12
 */
public interface ICategoryService {

    // 分页获取商品分类数据
    ResponseDTO<PageDTO<CategoryDTO>> getCategoryListByPage(PageDTO<CategoryDTO> pageDTO);

    // 保存商品分类数据(添加、修改)
    ResponseDTO<Boolean> saveCategory(CategoryDTO categoryDTO);

    // 删除商品分类数据
    ResponseDTO<Boolean> removeCategory(CategoryDTO categoryDTO);

    // 获取所有商品分类数据
    ResponseDTO<List<CategoryDTO>> getAllCategoryList();

    // 前台获取推荐商品分类
    ResponseDTO<List<CategoryDTO>> allCategory();

    // 根据id获取商品分类
    ResponseDTO<CategoryDTO> getById(CategoryDTO categoryDTO);

    // 获取五个成交额最高的商品分类
    ResponseDTO<List<CategoryDTO>> getCategoryListByPrice();
}
