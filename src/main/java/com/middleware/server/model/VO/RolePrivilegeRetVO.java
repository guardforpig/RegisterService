package com.middleware.server.model.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RolePrivilegeRetVO {
    private Long id;

    private RoleSimpleRetVo rolevo;

    private UserSimpleRetVo creatorvo;

    private PrivilegeSimpleRetVO privilegevo;

    @ApiModelProperty(name = "创建时间", value = "gmtCreate")
    private LocalDateTime gmtcreate;

    @ApiModelProperty(name = "创建时间", value = "gmtCreate")
    private LocalDateTime gmtmodified;

    public RolePrivilegeRetVO(Long id, PrivilegeSimpleRetVO privilegeVO,
                              RoleSimpleRetVo roleVO, UserSimpleRetVo creatorVO,
                              LocalDateTime gmtCreate, LocalDateTime gmt_modified) {
        this.id = id;
        this.privilegevo = privilegeVO;
        this.rolevo = roleVO;
        this.creatorvo = creatorVO;
        this.gmtcreate = gmtCreate;
        this.gmtmodified = gmt_modified;
    }

    public RolePrivilegeRetVO() {
    }
}
