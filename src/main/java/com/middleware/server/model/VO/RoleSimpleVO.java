package com.middleware.server.model.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoleSimpleVO {
    @ApiModelProperty(name = "角色名")
    String rolename;
    @ApiModelProperty(name = "角色描述")
    String description;

    public RoleSimpleVO(String name, String description) {
        this.rolename=name;
        this.description=description;
    }

    public RoleSimpleVO() {
    }
}
