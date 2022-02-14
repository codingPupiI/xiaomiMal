package com.lyf.programmer.service.impl;

import com.alipay.api.AlipayApiException;
import com.lyf.programmer.dao.OrderMapper;
import com.lyf.programmer.bean.CodeMsg;
import com.lyf.programmer.domain.AliPayBean;
import com.lyf.programmer.domain.Order;
import com.lyf.programmer.domain.OrderExample;
import com.lyf.programmer.dto.OrderDTO;
import com.lyf.programmer.dto.ResponseDTO;
import com.lyf.programmer.enums.OrderStateEnum;
import com.lyf.programmer.service.IAliPayService;
import com.lyf.programmer.util.AliPayUtil;
import com.lyf.programmer.util.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-12-02 11:46
 */
@Service
@Transactional
public class AliPayServiceImpl implements IAliPayService {

    @Resource
    private AliPayUtil aliPayUtil;

    @Resource
    private OrderMapper orderMapper;

    /**
     * 调用沙箱支付宝支付
     * @param orderDTO
     * @return
     * @throws AlipayApiException
     */
    @Override
    public String aliPay(OrderDTO orderDTO) throws AlipayApiException {
        AliPayBean alipayBean = new AliPayBean();
        Order order = orderMapper.selectByPrimaryKey(orderDTO.getId());
        alipayBean.setOut_trade_no(order.getTradeNo());
        alipayBean.setSubject("仿小米电子产品商城系统订单支付");
        alipayBean.setTotal_amount(String.valueOf(order.getTotalPrice()));
        alipayBean.setBody("欢迎您在仿小米电子产品商城系统上下单！！");
        return aliPayUtil.pay(alipayBean);
    }

    /**
     *  支付成功操作处理
     * @param orderDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> successPay(OrderDTO orderDTO) {
        if(CommonUtil.isEmpty(orderDTO.getTradeNo())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andTradeNoEqualTo(orderDTO.getTradeNo());
        List<Order> orderList = orderMapper.selectByExample(orderExample);
        if(orderList == null || orderList.size() != 1){
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_NOT_EXIST);
        }
        Order order = orderList.get(0);
        // 修改订单状态
        order.setState(OrderStateEnum.PAYED.getCode());
        if(orderMapper.updateByPrimaryKeySelective(order) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.ORDER_UPDATE_ERROR);
        }

        return ResponseDTO.successByMsg(true, "支付成功！");
    }
}
