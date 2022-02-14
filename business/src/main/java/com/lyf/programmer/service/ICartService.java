package com.lyf.programmer.service;

import com.lyf.programmer.dto.CartDTO;
import com.lyf.programmer.dto.ResponseDTO;

import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-11-30 16:51
 */
public interface ICartService {

    // 添加购物车操作处理
    ResponseDTO<Boolean> addCart(CartDTO cartDTO);

    // 删除购物车操作处理
    ResponseDTO<Boolean> deleteCart(CartDTO cartDTO);

    // 获取某用户的购物车商品信息
    ResponseDTO<List<CartDTO>> listCart(CartDTO cartDTO);

    // 更新购物车中的商品数量
    ResponseDTO<Boolean> updateQuantity(CartDTO cartDTO);
}
