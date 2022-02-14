package com.lyf.programmer.service;

import com.alipay.api.AlipayApiException;
import com.lyf.programmer.dto.OrderDTO;
import com.lyf.programmer.dto.ResponseDTO;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-12-02 11:46
 */
public interface IAliPayService {

    // 调用沙箱支付宝支付
    String aliPay(OrderDTO orderDTO) throws AlipayApiException;

    // 支付成功操作处理
    ResponseDTO<Boolean> successPay(OrderDTO orderDTO);
}
