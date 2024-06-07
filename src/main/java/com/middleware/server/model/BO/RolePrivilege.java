package com.middleware.server.model.BO;

import com.middleware.server.common.Common;
import com.middleware.server.model.PO.RolePrivilegePO;
import com.middleware.server.model.VO.RolePrivilegeRetVO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RolePrivilege {
    private Long id;
    private Privilege privilege;
    private  Role role;
    private User creator;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private String signature;

    public RolePrivilege(RolePrivilegePO rolePrivilegePO, Role role, Privilege privilege, User creator){
        this.id = rolePrivilegePO.getId();
        this.privilege =privilege;
        this.role = role;
        this.creator = creator;
        this.gmtCreate = rolePrivilegePO.getGmtCreate();
        this.gmtModified = rolePrivilegePO.getGmtModified();
        this.signature = rolePrivilegePO.getSignature();

        StringBuilder signature = Common.concatString("-",
                rolePrivilegePO.getRoleId().toString(), rolePrivilegePO.getPrivilegeId().toString(), rolePrivilegePO.getCreatorId().toString());
        this.signature = Common.getSHA256(signature.toString());
    }

    public RolePrivilege(RolePrivilegePO rolePrivilegePO) {
        this.id = rolePrivilegePO.getId();
        this.gmtCreate = rolePrivilegePO.getGmtCreate();
        this.gmtModified = rolePrivilegePO.getGmtModified();
        this.signature = rolePrivilegePO.getSignature();

        StringBuilder signature = Common.concatString("-",
                rolePrivilegePO.getRoleId().toString(), rolePrivilegePO.getPrivilegeId().toString(), rolePrivilegePO.getCreatorId().toString());
        this.signature = Common.getSHA256(signature.toString());
    }

    public RolePrivilegeRetVO toRetVO() {
        RolePrivilegeRetVO rolePrivilegeRetVO = new RolePrivilegeRetVO();
        rolePrivilegeRetVO.setId(this.id);
        rolePrivilegeRetVO.setPrivilegevo(this.privilege.createSimpleVO());
        rolePrivilegeRetVO.setCreatorvo(this.creator.createSimpleVO());
        rolePrivilegeRetVO.setRolevo(this.role.createSimpleVO());
        rolePrivilegeRetVO.setGmtcreate(this.gmtCreate);
        return rolePrivilegeRetVO;
    }
}
