package com.middleware.server.model.VO;

import com.middleware.server.model.PO.RolePrivilegePO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RolePrivilegeVO {
    private Long id;
    private Long roleid;
    private Long privilegeid;
    private Long creatorid;
    @ApiModelProperty(name = "创建时间", value = "gmtCreate")
    private LocalDateTime gmtcreate;
    @ApiModelProperty(name = "创建时间", value = "gmtCreate")
    private LocalDateTime gmtmodified;

    public RolePrivilegeVO(Long id, Long roleId, Long privilegeId, Long creatorId, LocalDateTime gmtCreate, LocalDateTime gmt_modified) {
        this.id = id;
        this.roleid = roleId;
        this.privilegeid = privilegeId;
        this.creatorid = creatorId;
        this.gmtcreate = gmtCreate;
        this.gmtmodified = gmt_modified;
    }

    public RolePrivilegeVO() {
    }

    public RolePrivilegeVO(RolePrivilegePO rolePrivilegePO) {
        id=rolePrivilegePO.getId();
        roleid=rolePrivilegePO.getRoleId();
        privilegeid=rolePrivilegePO.getPrivilegeId();
        creatorid=rolePrivilegePO.getPrivilegeId();
        gmtcreate=rolePrivilegePO.getGmtCreate();
        gmtmodified=rolePrivilegePO.getGmtModified();
    }
}
