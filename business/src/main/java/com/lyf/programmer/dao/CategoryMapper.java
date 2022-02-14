package com.lyf.programmer.dao;

import com.lyf.programmer.domain.Category;
import com.lyf.programmer.domain.CategoryExample;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

public interface CategoryMapper {
    BigInteger countByExample(CategoryExample example);

    int deleteByExample(CategoryExample example);

    int deleteByPrimaryKey(String id);

    int insert(Category record);

    int insertSelective(Category record);

    List<Category> selectByExample(CategoryExample example);

    Category selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Category record, @Param("example") CategoryExample example);

    int updateByExample(@Param("record") Category record, @Param("example") CategoryExample example);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
}