package com.lyf.programmer.dao;

import com.lyf.programmer.domain.User;
import com.lyf.programmer.domain.UserExample;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

public interface UserMapper {
    BigInteger countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(String id);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}