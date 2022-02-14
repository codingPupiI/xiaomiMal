package com.lyf.programmer.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyf.programmer.dao.*;
import com.lyf.programmer.dao.my.MyCartMapper;
import com.lyf.programmer.dao.my.MyOrderItemMapper;
import com.lyf.programmer.dao.my.MyOrderMapper;
import com.lyf.programmer.bean.CodeMsg;
import com.lyf.programmer.domain.*;
import com.lyf.programmer.dto.*;
import com.lyf.programmer.util.*;
import com.yjq.programmer.dao.*;
import com.yjq.programmer.domain.*;
import com.yjq.programmer.dto.*;
import com.lyf.programmer.enums.OrderStateEnum;
import com.lyf.programmer.service.IOrderService;
import com.yjq.programmer.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-12-02 11:45
 */
@Service
@Transactional
public class OrderServiceImpl implements IOrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private CartMapper cartMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private MyOrderItemMapper myOrderItemMapper;

    @Resource
    private MyCartMapper myCartMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private MyOrderMapper myOrderMapper;


    /**
     * 生成订单操作
     * @param cartIds
     * @return
     */
    @Override
    public ResponseDTO<OrderDTO> addOrder(List<String> cartIds) {
        if(cartIds.size() == 0){
            return ResponseDTO.errorByMsg(CodeMsg.CART_SELECT_EMPTY);
        }
        List<OrderItem> orderItemList = new ArrayList<>();
        Order order = new Order();
        String orderId = UuidUtil.getShortUuid();
        order.setId(orderId);
        BigDecimal totalPrice = new BigDecimal(0);
        for(String id : cartIds){
            Cart cart = cartMapper.selectByPrimaryKey(id);
            // 封装订单所属用户数据
            order.setUserId(cart.getUserId());
            // 核对商品库存是否满足
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if(product == null){
                CodeMsg codeMsg = CodeMsg.PRODUCT_NOT_EXIST;
                codeMsg.setMsg("商品：\""+product.getName()+"\"信息已不存在！");
                return ResponseDTO.errorByMsg(codeMsg);
            }
            if(product.getStock() < cart.getQuantity()){
                CodeMsg codeMsg = CodeMsg.PRODUCT_STOCK_ERROR;
                codeMsg.setMsg("商品：\""+product.getName()+"\"库存不足，请调整购买数量！");
                return ResponseDTO.errorByMsg(codeMsg);
            }
            // 封装订单详情数据
            OrderItem orderItem = new OrderItem();
            orderItem.setId(UuidUtil.getShortUuid());
            orderItem.setOrderId(orderId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductPhoto(product.getPhoto());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setSum(new BigDecimal(orderItem.getQuantity()).multiply(orderItem.getProductPrice()));
            orderItemList.add(orderItem);
            totalPrice = totalPrice.add(new BigDecimal(orderItem.getQuantity()).multiply(orderItem.getProductPrice()));
        }
        order.setCreateTime(new Date());
        order.setTradeNo(String.valueOf(new SnowFlake(2,3).nextId()));
        order.setState(OrderStateEnum.NO_PAY.getCode());
        order.setTotalPrice(totalPrice);
        // 插入订单数据
        if(orderMapper.insertSelective(order) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_ADD_ERROR);
        }
        // 插入订单详情数据
        if(myOrderItemMapper.batchInsert(orderItemList) == 0){
            throw new RuntimeException("生成订单失败，请稍后重试！");
        }
        // 清空购物车中选中的商品
        if(myCartMapper.batchDelete(cartIds) == 0){
            throw new RuntimeException("生成订单失败，请稍后重试！");
        }
        OrderDTO orderDTO = CopyUtil.copy(order, OrderDTO.class);
        return ResponseDTO.successByMsg(orderDTO, "订单生成成功！");
    }

    /**
     * 根据id获取订单信息
     * @param orderDTO
     * @return
     */
    @Override
    public ResponseDTO<OrderDTO> getById(OrderDTO orderDTO) {
        if(CommonUtil.isEmpty(orderDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Order order = orderMapper.selectByPrimaryKey(orderDTO.getId());
        if(order == null){
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_NOT_EXIST);
        }
        // 查询订单详情数据
        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.createCriteria().andOrderIdEqualTo(orderDTO.getId());
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
        OrderDTO selectOrderDTO = CopyUtil.copy(order, OrderDTO.class);
        selectOrderDTO.setOrderItemDTOList(CopyUtil.copyList(orderItemList, OrderItemDTO.class));
        return ResponseDTO.success(selectOrderDTO);
    }

    /**
     * 更新订单信息
     * @param orderDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> updateOrder(OrderDTO orderDTO) {
        if(CommonUtil.isEmpty(orderDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(orderDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        Order order = CopyUtil.copy(orderDTO, Order.class);
        // 再次核对库存并减少对应商品的库存信息
        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.createCriteria().andOrderIdEqualTo(order.getId());
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
        for(OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            if(product == null){
                CodeMsg codeMsg = CodeMsg.PRODUCT_NOT_EXIST;
                codeMsg.setMsg("商品：\""+product.getName()+"\"信息已不存在！");
                return ResponseDTO.errorByMsg(codeMsg);
            }
            if(product.getStock() < orderItem.getQuantity()){
                CodeMsg codeMsg = CodeMsg.PRODUCT_STOCK_ERROR;
                codeMsg.setMsg("商品：\""+product.getName()+"\"库存不足，请调整购买数量！");
                return ResponseDTO.errorByMsg(codeMsg);
            }
            product.setStock(product.getStock() - orderItem.getQuantity());
            // 更新商品库存信息
            if(productMapper.updateByPrimaryKeySelective(product) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_STOCK_UPDATE_ERROR);
            }
        }
        // 更新数据库中订单数据
        if(orderMapper.updateByPrimaryKeySelective(order) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_UPDATE_ERROR);
        }
        return ResponseDTO.success(true);
    }

    /**
     * 根据用户id获取订单信息
     * @param orderDTO
     * @return
     */
    @Override
    public ResponseDTO<List<OrderDTO>> getByUserId(OrderDTO orderDTO) {
        if(CommonUtil.isEmpty(orderDTO.getUserId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andUserIdEqualTo(orderDTO.getUserId());
        orderExample.setOrderByClause("create_time desc");
        List<Order> orderList = orderMapper.selectByExample(orderExample);
        List<OrderDTO> orderDTOList = CopyUtil.copyList(orderList, OrderDTO.class);
        return ResponseDTO.success(orderDTOList);
    }

    /**
     * 根据订单id获取订单详情信息
     * @param orderDTO
     * @return
     */
    @Override
    public ResponseDTO<OrderDTO> getOrderItemByOrderId(OrderDTO orderDTO) {
        if(CommonUtil.isEmpty(orderDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Order order = orderMapper.selectByPrimaryKey(orderDTO.getId());
        orderDTO = CopyUtil.copy(order, OrderDTO.class);
        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.createCriteria().andOrderIdEqualTo(orderDTO.getId());
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
        List<OrderItemDTO> orderItemDTOList = CopyUtil.copyList(orderItemList, OrderItemDTO.class);
        orderDTO.setOrderItemDTOList(orderItemDTOList);
        return ResponseDTO.success(orderDTO);
    }

    /**
     * 修改订单状态操作
     * @param orderDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> updateOrderState(OrderDTO orderDTO) {
        if(orderDTO.getState() == null || CommonUtil.isEmpty(orderDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Order order = orderMapper.selectByPrimaryKey(orderDTO.getId());
        if(orderDTO.getState().equals(OrderStateEnum.CANCELED.getCode())){
            // 如果修改为"已取消"状态，则恢复库存信息
            OrderItemExample orderItemExample = new OrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(order.getId());
            List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
            for(OrderItem orderItem : orderItemList){
                Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
                if(product != null){
                    product.setStock(product.getStock() + orderItem.getQuantity());
                    if(productMapper.updateByPrimaryKeySelective(product) == 0){
                        return ResponseDTO.errorByMsg(CodeMsg.PRODUCT_STOCK_UPDATE_ERROR);
                    }
                }
            }
        }
        // 修改订单状态
        order.setState(orderDTO.getState());
        if(orderMapper.updateByPrimaryKeySelective(order) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_UPDATE_ERROR);
        }
        return ResponseDTO.successByMsg(true, "成功修改订单状态！");
    }

    /**
     * 分页获取订单数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<OrderDTO>> getOrderListByPage(PageDTO<OrderDTO> pageDTO) {
        OrderExample orderExample = new OrderExample();
        // 判断是否进行关键字搜索
        if(!CommonUtil.isEmpty(pageDTO.getSearchContent())){
            orderExample.createCriteria().andTradeNoLike("%"+pageDTO.getSearchContent()+"%");
        }
        orderExample.setOrderByClause("create_time desc");
        // 不知道当前页多少，默认为第一页
        if(pageDTO.getPage() == null){
            pageDTO.setPage(1);
        }
        pageDTO.setSize(5);
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出订单数据
        List<Order> orderList = orderMapper.selectByExample(orderExample);
        PageInfo<Order> pageInfo = new PageInfo<>(orderList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 讲domain类型数据  转成 DTO类型数据
        List<OrderDTO> orderDTOList = CopyUtil.copyList(orderList, OrderDTO.class);
        for(OrderDTO orderDTO : orderDTOList){
            User user = userMapper.selectByPrimaryKey(orderDTO.getUserId());
            orderDTO.setUserDTO(CopyUtil.copy(user, UserDTO.class));
        }
        pageDTO.setList(orderDTOList);
        return ResponseDTO.success(pageDTO);
    }

    /**
     * 后台删除订单数据
     * @param orderDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeOrder(OrderDTO orderDTO) {
        if(CommonUtil.isEmpty(orderDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 删除订单数据
        if(orderMapper.deleteByPrimaryKey(orderDTO.getId()) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_DELETE_ERROR);
        }
        // 删除订单详情数据
        OrderItemExample orderItemExample = new OrderItemExample();
        orderItemExample.createCriteria().andOrderIdEqualTo(orderDTO.getId());
        if(orderItemMapper.deleteByExample(orderItemExample) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_DELETE_ERROR);
        }
        return ResponseDTO.successByMsg(true, "删除成功");
    }

    /**
     * 获取订单总数
     * @return
     */
    @Override
    public ResponseDTO<BigInteger> getOrderTotal() {
        BigInteger total = orderMapper.countByExample(new OrderExample());
        return ResponseDTO.success(total);
    }

    /**
     * 获取今天订单成交金额
     * @return
     */
    @Override
    public ResponseDTO<BigDecimal> getTodayPrice() {
        return ResponseDTO.success(myOrderMapper.todayTotalPrice());
    }

    /**
     * 获取本周订单成交金额
     * @return
     */
    @Override
    public ResponseDTO<BigDecimal> getWeekPrice() {
        return ResponseDTO.success(myOrderMapper.weekTotalPrice());
    }

    /**
     * 获取本月订单成交金额
     * @return
     */
    @Override
    public ResponseDTO<BigDecimal> getMonthPrice() {
        return ResponseDTO.success(myOrderMapper.monthTotalPrice());
    }

    @Override
    public ResponseDTO<List<Integer>> getOrderCountByDateAndState() {
        List<Integer> totalList = new ArrayList<>();
        Map<String, Object> queryMap = new HashMap<>();
        List<Integer> finishStateList = new ArrayList<>();
        finishStateList.add(2);
        finishStateList.add(3);
        finishStateList.add(4);
        List<Integer> failStateList = new ArrayList<>();
        failStateList.add(1);
        failStateList.add(5);
        // 获取当天已完成的收益次数
        queryMap.put("start", 0);
        queryMap.put("end", -1);
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, finishStateList));
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, failStateList));
        // 获取昨天已完成的收益次数
        queryMap.put("start", 1);
        queryMap.put("end", 0);
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, finishStateList));
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, failStateList));
        // 获取前天已完成的收益次数
        queryMap.put("start", 2);
        queryMap.put("end", 1);
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, finishStateList));
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, failStateList));
        // 获取大前天已完成的收益次数
        queryMap.put("start", 3);
        queryMap.put("end", 2);
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, finishStateList));
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, failStateList));
        // 获取大大前天已完成的收益次数
        queryMap.put("start", 4);
        queryMap.put("end", 3);
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, finishStateList));
        totalList.add(myOrderMapper.getOrderTotalByDateAndState(queryMap, failStateList));
        return ResponseDTO.success(totalList);
    }
}
