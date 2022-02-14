package com.lyf.programmer.controller.web;

import com.lyf.programmer.dto.OrderDTO;
import com.lyf.programmer.dto.ResponseDTO;
import com.lyf.programmer.service.IOrderService;
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
 * @create 2021-12-02 11:36
 */
@RestController("WebOrderController")
@RequestMapping("/web/order")
public class OrderController {

    @Resource
    private IOrderService orderService;

    /**
     * 添加订单操作
     * @param cartIds
     * @return
     */
    @PostMapping("/add")
    public ResponseDTO<OrderDTO> addOrder(@RequestBody List<String> cartIds){
        return orderService.addOrder(cartIds);
    }

    /**
     * 根据id获取订单信息
     * @param orderDTO
     * @return
     */
    @PostMapping("/get")
    public ResponseDTO<OrderDTO> getById(@RequestBody OrderDTO orderDTO){
        return orderService.getById(orderDTO);
    }

    /**
     * 更新订单信息
     * @param orderDTO
     * @return
     */
    @PostMapping("/update")
    public ResponseDTO<Boolean> updateOrder(@RequestBody OrderDTO orderDTO){
        return orderService.updateOrder(orderDTO);
    }

    /**
     * 获取个人订单信息
     * @param orderDTO
     * @return
     */
    @PostMapping("/self-order")
    public ResponseDTO<List<OrderDTO>> getByUserId(@RequestBody OrderDTO orderDTO){
        return orderService.getByUserId(orderDTO);
    }

    /**
     * 根据订单id获取订单详情信息
     * @param orderDTO
     * @return
     */
    @PostMapping("/order-item")
    public ResponseDTO<OrderDTO> getOrderItemByOrderId(@RequestBody OrderDTO orderDTO){
        return orderService.getOrderItemByOrderId(orderDTO);
    }

    /**
     * 取消订单操作
     * @param orderDTO
     * @return
     */
    @PostMapping("/cancel")
    public ResponseDTO<Boolean> cancelOrder(@RequestBody OrderDTO orderDTO){
        return orderService.updateOrderState(orderDTO);
    }
}
