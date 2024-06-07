package com.middleware.server.model.VO;

import com.middleware.server.model.PO.UserRolePO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UserRoleRetVO {
    @ApiModelProperty(name = "用户id", value = "userid")
    private Long id;

    private UserSimpleRetVo user;

    private RoleSimpleRetVo role;

    private UserSimpleRetVo creator;

    @ApiModelProperty(name = "创建时间", value = "gmtCreate")
    private LocalDateTime gmtcreate;

    @ApiModelProperty(name = "创建时间", value = "gmtCreate")
    private LocalDateTime gmtmodified;


    public UserRoleRetVO(UserRolePO userRolePO, UserSimpleRetVo userSimpleRetVo, RoleSimpleRetVo roleSimpleRetVo, UserSimpleRetVo creatorUser) {
        id=userRolePO.getId();
        user=userSimpleRetVo;
        role=roleSimpleRetVo;
        creator=creatorUser;
        gmtcreate=userRolePO.getGmtCreate();
        gmtmodified=userRolePO.getGmtModified();
    }

    public UserRoleRetVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserSimpleRetVo getUser() {
        return user;
    }

    public void setUser(UserSimpleRetVo user) {
        this.user = user;
    }

    public RoleSimpleRetVo getRole() {
        return role;
    }

    public void setRole(RoleSimpleRetVo role) {
        this.role = role;
    }

    public UserSimpleRetVo getCreator() {
        return creator;
    }

    public void setCreator(UserSimpleRetVo creator) {
        this.creator = creator;
    }

}
