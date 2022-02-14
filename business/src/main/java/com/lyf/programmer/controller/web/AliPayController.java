package com.lyf.programmer.controller.web;

import com.alipay.api.AlipayApiException;
import com.lyf.programmer.dto.OrderDTO;
import com.lyf.programmer.dto.ResponseDTO;
import com.lyf.programmer.service.IAliPayService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-11-07 10:37
 */
@RestController("webAliPayController")
@RequestMapping("/web/pay")
public class AliPayController {

    @Resource
    private IAliPayService aliPayService;

    /**
     * 支付宝支付数据封装
     * @param orderDTO
     * @return
     * @throws AlipayApiException
     */
    @PostMapping(value = "/aliPay")
    public String aliPay(@RequestBody OrderDTO orderDTO) throws AlipayApiException {
        return aliPayService.aliPay(orderDTO);
    }

    /**
     * 支付成功操作处理
     * @param orderDTO
     * @return
     */
    @PostMapping(value = "/success")
    public ResponseDTO<Boolean> successPay(@RequestBody OrderDTO orderDTO){
        return aliPayService.successPay(orderDTO);
    }
}
