package com.lyf.programmer.controller.web;

import com.lyf.programmer.dto.ProductDTO;
import com.lyf.programmer.dto.ResponseDTO;
import com.lyf.programmer.service.IProductService;
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
 * @create 2021-11-30 9:13
 */
@RestController("WebProductController")
@RequestMapping("/web/product")
public class ProductController {

    @Resource
    private IProductService productService;

    /**
     * 前台根据商品id获取商品
     * @param productDTO
     * @return
     */
    @PostMapping("/get")
    public ResponseDTO<ProductDTO> getById(@RequestBody ProductDTO productDTO){
        return productService.getById(productDTO);
    }

    /**
     * 前台根据商品分类或者搜索内容获取商品列表数据
     * @param productDTO
     * @return
     */
    @PostMapping("/list")
    public ResponseDTO<List<ProductDTO>> getByCategoryOrSearch(@RequestBody ProductDTO productDTO){
        return productService.getByCategoryOrSearch(productDTO);
    }

}
