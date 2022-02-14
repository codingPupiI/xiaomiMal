package com.lyf.programmer.dao;

import com.lyf.programmer.domain.OrderItem;
import com.lyf.programmer.domain.OrderItemExample;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

public interface OrderItemMapper {
    BigInteger countByExample(OrderItemExample example);

    int deleteByExample(OrderItemExample example);

    int deleteByPrimaryKey(String id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    List<OrderItem> selectByExample(OrderItemExample example);

    OrderItem selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") OrderItem record, @Param("example") OrderItemExample example);

    int updateByExample(@Param("record") OrderItem record, @Param("example") OrderItemExample example);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
}