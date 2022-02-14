package com.lyf.programmer.service;

import com.lyf.programmer.dto.OrderDTO;
import com.lyf.programmer.dto.PageDTO;
import com.lyf.programmer.dto.ResponseDTO;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-12-02 11:45
 */
public interface IOrderService {

    // 生成订单操作
    ResponseDTO<OrderDTO> addOrder(List<String> cartIds);

    // 根据id获取订单信息
    ResponseDTO<OrderDTO> getById(OrderDTO orderDTO);

    // 更新订单信息
    ResponseDTO<Boolean> updateOrder(OrderDTO orderDTO);

    // 根据用户id获取订单信息
    ResponseDTO<List<OrderDTO>> getByUserId(OrderDTO orderDTO);

    // 根据订单id获取订单详情信息
    ResponseDTO<OrderDTO> getOrderItemByOrderId(OrderDTO orderDTO);

    // 修改订单状态操作
    ResponseDTO<Boolean> updateOrderState(OrderDTO orderDTO);

    // 分页获取订单数据
    ResponseDTO<PageDTO<OrderDTO>> getOrderListByPage(PageDTO<OrderDTO> pageDTO);

    // 后台删除订单数据
    ResponseDTO<Boolean> removeOrder(OrderDTO orderDTO);

    // 获取订单总数
    ResponseDTO<BigInteger> getOrderTotal();

    // 获取今天订单成交金额
    ResponseDTO<BigDecimal> getTodayPrice();

    // 获取本周订单成交金额
    ResponseDTO<BigDecimal> getWeekPrice();

    // 获取本月订单成交金额
    ResponseDTO<BigDecimal> getMonthPrice();

    // 根据时间范围和订单状态获取交易的订单总数
    ResponseDTO<List<Integer>> getOrderCountByDateAndState();
}
