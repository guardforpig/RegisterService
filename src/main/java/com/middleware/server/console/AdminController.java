package com.middleware.server.console;

import com.middleware.server.annotation.Audit;
import com.middleware.server.annotation.LoginUser;
import com.middleware.server.common.Common;
import com.middleware.server.common.ReturnObject;
import com.middleware.server.model.VO.RoleVO;
import com.middleware.server.console.AdminService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value="/admin",produces = "application/json;charset=UTF-8",consumes = "application/json;charset=UTF-8")
public class AdminController {
    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

//-----------------------------------用户部分（包含用户×角色）---------------------------------------------------------------------------------------------
    /**
     * 管理员查看所有用户
     * @param page 页码
     * @param pageSize 一页条目数量
     * @return
     * @author GXC
     */
    @Audit
    @ApiOperation(value = "管理员查看所有用户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pagesize", value = "每页的条目数", required = false)
    })
    @GetMapping("/user")
    public Object getAllUser( @RequestParam(name="page",required = false) Integer page,
                             @RequestParam(name = "pageSize",required = false) Integer pageSize)
    {
        if(page==null) page=1;
        if(pageSize==null) pageSize=2;
        ReturnObject returnObject = null;
        return Common.getPageGenericRetObject(adminService.getAllUser(page,pageSize));
    }
    /**
     * 新建用户
     * @param user 用户信息
     * @return
     * @author GXC at 13:03 2021/7/18
     */
    @Audit
    @ApiOperation(value = "管理员新增用户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "NewUserBodyBody", name = "user", value = "新建用户的信息", required = true)
    })
    @PostMapping("/user")
    public Object createUser(@RequestBody(required = true) NewUserBody user,@LoginUser Long creatorId){
        ReturnObject returnObject=adminService.newUser(user.getUsername(),user.getRoleNameList(),creatorId);
        return returnObject;
    }
    /**
     * 管理员查看所有用户
     * @param thisUserId 本用户id
     * @param targetId 操作目标用户id
     * @author GXC at 15:03 2021/7/18
     */
    @Audit
    @ApiOperation(value = "管理员查看特定用户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "targetId", value = "查询目标用户的ID", required = true)
    })
    @GetMapping("/user/{userid}")
    public Object getSingleUser(@LoginUser Long thisUserId,@PathVariable(value="userid")Long targetId){
        ReturnObject returnObject=null;
        returnObject= adminService.getSingleUser(targetId);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员逻辑删除用户(state=3)
     * @param thisUserId 本用户id
     * @param targetId 操作目标用户id
     * @return
     * @author GXC at 17:39 2021/7/18
     */
    @Audit
    @ApiOperation(value = "管理员逻辑删除用户(state=3,disable=1)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "userid", value = "删除目标用户的ID", required = true)
    })
    @DeleteMapping("/user/{userid}")
    public Object banUser(@LoginUser Long thisUserId,@PathVariable(value = "userid")Long targetId){
        ReturnObject returnObject=null;
        returnObject= adminService.banUser(targetId);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员更改用户状态(state1/2切换)
     * @param thisUserId 本用户id
     * @param targetId 操作目标用户id
     * @return
     * @author GXC at 23:41 2021/7/18
     */
    @Audit
    @ApiOperation(value = "管理员更改用户状态(state1/2切换)正常状态与过渡状态切换")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "userid", value = "删除目标用户的ID", required = true)
    })
    @PutMapping("/user/{userid}/state")
    public Object reverseUser(@LoginUser Long thisUserId,@PathVariable(value = "userid")Long targetId){
        ReturnObject returnObject=null;
        returnObject= adminService.reverseUser(targetId);
        return Common.decorateReturnObject(returnObject);
    }
    /**
     * 管理员初始化用户密码，暂定123456
     * @param thisUserId 本用户id
     * @param targetId 目标用户id
     * @return
     * @author GXC at 23:41 2021/7/18
     */
    @Audit
    @ApiOperation(value = "管理员初始化用户密码，暂定123456")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "userid", value = "删除目标用户的ID", required = true)
    })
    @PutMapping("/user/{userid}/password")
    public Object resetUserPassword(@LoginUser Long thisUserId,@PathVariable(value = "userid")Long targetId){
        ReturnObject returnObject=null;
        returnObject= adminService.resetUserPassword(targetId);
        return Common.decorateReturnObject(returnObject);
    }
    /**
     * 管理员批量修改员工角色（已有删除，未有则添加）
     * @param thisUserId 操作者id
     * @param targetId 操作目标用户id
     * @param roleIdList 角色列表
     * @return
     * @author GXC at 9:32 2021/7/19
     */
    @Audit
    @ApiOperation(value = "管理员批量修改员工角色（已有删除，未有则添加）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "userid", value = "操作目标用户的ID", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "List", name = "roles", value = "操作涉及角色列表", required = true)
    })
    @PutMapping("/user/{userid}/roles")
    public Object modifyUserRole(@LoginUser Long thisUserId,@PathVariable(value = "userid")Long targetId,@RequestBody(required = false) RoleIdListBody roleIdList){
        ReturnObject returnObject=null;
        returnObject= adminService.modifyUserRole(targetId,roleIdList.getRoleIdList(),thisUserId);
        return Common.decorateReturnObject(returnObject);
    }
//-----------------------------------角色部分（包含角色×权限）---------------------------------------------------------------------------------------------
    /**
     * 管理员查看所有角色
     * @param userId 本用户id
     * @param page 页码
     * @param pageSize 一页条目数量
     * @return
     * @author GXC
     */
    @Audit
    @ApiOperation(value = "管理员查看所有角色")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pagesize", value = "每页的条目数", required = false)
    })
    @GetMapping("/roles")
    public Object getAllRole(@LoginUser Long userId,@RequestParam(value="page",required = false) Integer page,
                             @RequestParam(value = "pageSize",required = false) Integer pageSize)
    {
        if(page==null) page=1;
        if(pageSize==null) pageSize=2;
        ReturnObject<PageInfo<RoleVO>> roles = adminService.getAllRole(page,pageSize);
//        log.info(""+roles.getData().getList().size());
        return Common.getPageGenericRetObject(roles);
    }
    /**
     * 管理员修改角色描述
     * @param userId
     * @param roleId
     * @param description
     * @return
     * @Author GXC at 17:04 2021/7/19
     */
    @Audit
    @ApiOperation(value = "管理员修改角色描述")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "roleid", value = "角色id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "description", value = "角色描述", required = false)
    })
    @PutMapping("/roles/{roleid}")
    public Object modifyRoleDescription(@LoginUser Long userId,@PathVariable(value = "roleid",required = true)Long roleId,
                                        @RequestParam(value = "description",required = false) String description)
    {
        ReturnObject returnObject=null;
        returnObject=adminService.modifyRoleDescription(roleId,description,userId);
        return Common.decorateReturnObject(returnObject);
    }
    /**
     * 管理员批量修改员工角色（已有删除，未有则添加）
     * @param thisUserId 操作者id
     * @param roleId 操作目标用户id
     * @param privilegeIdListBody 角色列表包装类
     * @return
     * @author GXC at 9:32 2021/7/19
     */
    @Audit
    @ApiOperation(value = "管理员批量修改角色权限（已有删除，未有则添加）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "roleid", value = "操作目标角色的ID", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "List", name = "privileges", value = "操作涉及权限列表", required = true)
    })
    @PutMapping("/roles/{roleid}/privileges")
    public Object modifyRolePrivilege(@LoginUser Long thisUserId,@PathVariable(value = "roleid")Long roleId,@RequestBody(required = false) PrivilegeIdListBody privilegeIdListBody){
        ReturnObject returnObject=null;
        returnObject= adminService.modifyRolePrivilege(roleId,privilegeIdListBody.getPrivilegeIdList(),thisUserId);
        return Common.decorateReturnObject(returnObject);
    }
    //-----------------------权限部分----------------------------------------------------------------------
    /**
     * 管理员查看所有权限
     * @param thisUserId
     * @return
     */
    @Audit
    @ApiOperation(value = "管理员查看所有权限")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true)
    })
    @GetMapping("/privileges")
    public Object getPrivilege(@LoginUser Long thisUserId){
        ReturnObject returnObject=null;
        returnObject= adminService.getPrivilege();
        return Common.decorateReturnObject(returnObject);
    }
}


//-----------------------------------------------------------------------------------------------------------------------
//-----------------------------Body接收类--------------------------------------------------------------------------------
@Data
class RoleID{
    /**
     * 角色id列表
     */
    private Long role;

}
/**
 * @author GXC
 */
@Data
class RoleIdListBody{
    private List<Long> roleIdList;
}
@Data
class NewUserBodyBody{
    NewUserBody data;
}
@Data
class NewUserBody{
    /**
     * 用户的所有角色
     */
    private List<String> roleNameList;
    /**
     * 用户名
     */
    private String username;

}
@Data
class PrivilegeIdListBody{
    private List<Long> privilegeIdList;
}
@Data
class StaffIdBody{
    private Long staffid;
}
