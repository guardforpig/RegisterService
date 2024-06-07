package com.middleware.server.console;

import com.middleware.server.annotation.Audit;
import com.middleware.server.annotation.LoginUser;
import com.middleware.server.common.Common;
import com.middleware.server.common.ReturnObject;
import com.middleware.server.model.VO.LoginVO;
import com.middleware.server.model.VO.TokenRetVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8",consumes = "application/json;charset=UTF-8")
public class UserController {
    @Autowired
    UserService userService;
    @PostMapping("/login")
    public Object login(@Validated @RequestBody LoginVO loginVo, HttpServletResponse response) {
        System.out.println(loginVo);
        ReturnObject returnObject = null;
        returnObject = userService.userLogin(loginVo.getUserName(), loginVo.getPassword());
        if (returnObject.getData() != null) {
            TokenRetVO vo = (TokenRetVO) returnObject.getData();
            response.addHeader("token", vo.getToken());
        }
        return Common.decorateReturnObject(returnObject);
    }

    @Audit
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "LoginVO", name = "loginVO", value = "登录信息", required = true)
    })
    @ApiOperation(value = "用户登出")
    @GetMapping("/userLogout")
    public Object logout(@LoginUser Long userId) {
        ReturnObject returnObject = null;
        returnObject = userService.userLogout(userId);
        return Common.decorateReturnObject(returnObject);
    }
    //----------------------------------------------------------------------------------------
    //用户与角色部分

    /**
     * 用户新增角色
     *
     * @param cid
     * @param userid
     * @param roleid
     * @return
     * @author tong
     */
    @Audit
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "cid", value = "创建者id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "user", value = "被赋予权限的用户id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "role", value = "赋予的权限的id", required = true)
    })
    @ApiOperation(value = "用户新增角色")
    @PostMapping("/users/{cid}/beusers/{userid}/roles/{roleid}")
    public Object userAddRole(@PathVariable Long cid, @PathVariable Long userid, @PathVariable Long roleid) {
        ReturnObject returnObject = null;
        returnObject = userService.userAddRole(cid, userid, roleid);
        return Common.decorateReturnObject(returnObject);
    }


    /**
     * 删除某个用户的某个角色
     *
     * @param uid 用户id
     * @param rid 角色id
     * @return Create By GXC
     */
    @Audit
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "userid", value = "用户id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "roleid", value = "角色id", required = true)
    })
    @ApiOperation(value = "删除某个用户的某个角色")
    @DeleteMapping("/user_id/{userid}/role_id/{roleid}")
    public Object deleteUserRole(@PathVariable("userid") Long uid, @PathVariable("roleid") Long rid) {
        ReturnObject returnObject = null;
        returnObject = userService.deleteUserRole(uid, rid);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 查询某个用户所拥有角色信息
     *
     * @param uid 发出查询的用户id
     * @param tid 查询目标用户id
     * @return Create By GXC
     */
    @Audit
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "uid", value = "查询用户的id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "tid", value = "查询目标用户的id", required = true)
    })
    @ApiOperation(value = "某用户查看某用户的角色")
    @GetMapping("/users/{uid}/target/{tid}")
    public Object searchAddRole(@PathVariable("uid") Long uid, @PathVariable("tid") Long tid) {
        ReturnObject returnObject = null;
        returnObject = userService.selectUserRoleByuId(tid);
        return Common.decorateReturnObject(returnObject);
    }
}
