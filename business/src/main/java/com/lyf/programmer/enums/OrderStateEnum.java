package com.lyf.programmer.enums;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2020-11-16 16:43
 */
/**
 * 订单状态枚举类：1：未支付；2：已支付，待发货；3：已发货，待收货；4：已收货；5：已取消
 * @author 82320
 *
 */
public enum OrderStateEnum {

    NO_PAY(1,"未支付"),

    PAYED(2,"已支付，待发货"),

    SEND(3, "已发货，待收货"),

    SIGN(4, "已收货"),

    CANCELED(5,"已取消"),

            ;

    Integer code;

    String desc;

    OrderStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
