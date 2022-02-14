package com.lyf.programmer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动类
 */
@SpringBootApplication
@MapperScan("com.yjq.programmer.dao")
public class ShoppingManageSystem
{
    public static void main( String[] args )
    {
        SpringApplication.run(ShoppingManageSystem.class, args);
    }

}


