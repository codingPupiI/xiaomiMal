package com.lyf.programmer.service;

import com.lyf.programmer.dto.PageDTO;
import com.lyf.programmer.dto.ProductDTO;
import com.lyf.programmer.dto.ResponseDTO;

import java.math.BigInteger;
import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-11-29 12:26
 */
public interface IProductService {

    // 分页获取商品数据
    ResponseDTO<PageDTO<ProductDTO>> getProductListByPage(PageDTO<ProductDTO> pageDTO);

    // 保存商品数据(添加、修改)
    ResponseDTO<Boolean> saveProduct(ProductDTO productDTO);

    // 删除商品数据
    ResponseDTO<Boolean> removeProduct(ProductDTO productDTO);

    // 根据商品id获取商品
    ResponseDTO<ProductDTO> getById(ProductDTO productDTO);

    // 根据商品分类或者搜索内容获取商品列表数据
    ResponseDTO<List<ProductDTO>> getByCategoryOrSearch(ProductDTO productDTO);

    // 获取商品总数
    ResponseDTO<BigInteger> getProductTotal();

    // 获取库存最少的三个商品
    ResponseDTO<List<ProductDTO>> getProductListByStock();

}
