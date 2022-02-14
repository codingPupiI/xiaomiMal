package com.lyf.programmer.controller.web;

import com.lyf.programmer.dto.CategoryDTO;
import com.lyf.programmer.dto.ResponseDTO;
import com.lyf.programmer.service.ICategoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-11-29 19:11
 */
@RestController("WebCategoryController")
@RequestMapping("/web/category")
public class CategoryController {

    @Resource
    private ICategoryService categoryService;

    /**
     * 前台首页获取推荐商品分类
     * @return
     */
    @PostMapping("/all")
    public ResponseDTO<List<CategoryDTO>> allCategory(){
        return categoryService.allCategory();
    }

    /**
     * 前台根据id获取商品分类
     * @param categoryDTO
     * @return
     */
    @PostMapping("/get")
    public ResponseDTO<CategoryDTO> getById(@RequestBody CategoryDTO categoryDTO){
        return categoryService.getById(categoryDTO);
    }
}
