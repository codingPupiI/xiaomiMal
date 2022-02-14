package com.lyf.programmer.dao;

import com.lyf.programmer.domain.Order;
import com.lyf.programmer.domain.OrderExample;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

public interface OrderMapper {
    BigInteger countByExample(OrderExample example);

    int deleteByExample(OrderExample example);

    int deleteByPrimaryKey(String id);

    int insert(Order record);

    int insertSelective(Order record);

    List<Order> selectByExample(OrderExample example);

    Order selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Order record, @Param("example") OrderExample example);

    int updateByExample(@Param("record") Order record, @Param("example") OrderExample example);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
}