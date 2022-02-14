package com.lyf.programmer.controller.web;

import com.lyf.programmer.dto.ResponseDTO;
import com.lyf.programmer.dto.UserDTO;
import com.lyf.programmer.service.IUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-11-27 10:14
 */
@RestController("WebUserController")
@RequestMapping("/web/user")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 前台登录操作处理
     * @param userDTO
     * @return
     */
    @PostMapping("/login")
    public ResponseDTO<UserDTO> login(@RequestBody UserDTO userDTO){
        return userService.webLogin(userDTO);
    }

    /**
     * 前台注册操作处理
     * @param userDTO
     * @return
     */
    @PostMapping("/register")
    public ResponseDTO<Boolean> register(@RequestBody UserDTO userDTO){
        return userService.register(userDTO);
    }

    /**
     * 前台检查用户是否登录
     * @param userDTO
     * @return
     */
    @PostMapping("/check_login")
    public ResponseDTO<UserDTO> checkLogin(@RequestBody UserDTO userDTO){
        return userService.checkLogin(userDTO);
    }

    /**
     * 前台用户退出登录
     * @return
     */
    @PostMapping("/logout")
    public ResponseDTO<Boolean> logout(@RequestBody UserDTO userDTO){
        return userService.logout(userDTO);
    }

    /**
     * 前台用户修改个人信息
     * @param userDTO
     * @return
     */
    @PostMapping("/update")
    public ResponseDTO<Boolean> updateUserInfo(@RequestBody UserDTO userDTO){
        return userService.updateUserInfo(userDTO);
    }
}
