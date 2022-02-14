package com.lyf.programmer.service;

import com.lyf.programmer.dto.PageDTO;
import com.lyf.programmer.dto.ResponseDTO;
import com.lyf.programmer.dto.UserDTO;

import java.math.BigInteger;
import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-11-18 15:48
 */
public interface IUserService {

    // 分页获取用户数据
    ResponseDTO<PageDTO<UserDTO>> getUserListByPage(PageDTO<UserDTO> pageDTO);

    // 保存用户数据(添加、修改)
    ResponseDTO<Boolean> saveUser(UserDTO userDTO);

    // 删除用户数据
    ResponseDTO<Boolean> removeUser(UserDTO userDTO);

    // 前台登录操作
    ResponseDTO<UserDTO> webLogin(UserDTO userDTO);

    // 后台登录操作
    ResponseDTO<UserDTO> adminLogin(UserDTO userDTO);

    // 前台注册操作
    ResponseDTO<Boolean> register(UserDTO userDTO);

    // 检查用户是否登录
    ResponseDTO<UserDTO> checkLogin(UserDTO userDTO);

    // 退出登录操作
    ResponseDTO<Boolean> logout(UserDTO userDTO);

    // 修改个人信息操作
    ResponseDTO<Boolean> updateUserInfo(UserDTO userDTO);

    // 获取用户总数
    ResponseDTO<BigInteger> getUserTotal();

    // 获取在线用户
    ResponseDTO<List<UserDTO>> getOnlineUser();

}
