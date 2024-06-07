package com.middleware.server.model.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author GXC at 15:22 2021/7/18
 */
@Data
public class UserWholeVO {
    @ApiModelProperty(value = "用户id")
    private Long userid;
    @ApiModelProperty(value="用户名")
    private String userName;
    @ApiModelProperty(value = "账号状态")
    private Integer state;
    @ApiModelProperty(value = "所拥有的角色")
    private List<RoleSimpleVO> roles;
    @ApiModelProperty(value = "最近登录记录")
    private String lastlogin;

    public UserWholeVO(Long userId, String name,Integer state,
                       List<RoleSimpleVO> roles, String lastLogin) {
        this.userid = userId;
        this.userName=name;
        this.state = state;
        this.roles = roles;
        this.lastlogin = lastLogin;
    }

    public UserWholeVO() {
    }
}
