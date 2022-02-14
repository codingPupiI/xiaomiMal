package com.lyf.programmer.service;

import com.lyf.programmer.dto.PageDTO;
import com.lyf.programmer.dto.ResponseDTO;
import com.lyf.programmer.dto.RoleDTO;

import java.util.List;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-11-18 15:43
 */
public interface IRoleService {

    // 分页获取角色数据
    ResponseDTO<PageDTO<RoleDTO>> getRoleListByPage(PageDTO<RoleDTO> pageDTO);

    // 保存角色数据(添加、修改)
    ResponseDTO<Boolean> saveRole(RoleDTO roleDTO);

    // 删除角色数据
    ResponseDTO<Boolean> removeRole(RoleDTO roleDTO);

    // 获取所有角色数据
    ResponseDTO<List<RoleDTO>> getAllRoleList();

}
