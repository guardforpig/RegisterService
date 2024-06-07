package com.middleware.server.model.BO;

import com.middleware.server.common.Common;
import com.middleware.server.model.PO.UserRolePO;
import com.middleware.server.model.VO.UserRoleRetVO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRole {
    private Long id;

    private User user;

    private Role role;

    private User creator;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String signature;

    public UserRole(UserRolePO userRolePo, User user, Role role, User creator){
        this.id = userRolePo.getId();
        this.user = user;
        this.role = role;
        this.creator = creator;
        this.gmtCreate = userRolePo.getGmtCreate();
        this.gmtModified = userRolePo.getGmtModified();
        this.signature = userRolePo.getSignature();

        StringBuilder signature = Common.concatString("-",
                userRolePo.getUserId().toString(), userRolePo.getRoleId().toString(), userRolePo.getCreatorId().toString());
        this.signature = Common.getSHA256(signature.toString());
    }

    public UserRole(UserRolePO po) {
        this.id = po.getId();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
        this.signature = po.getSignature();

        StringBuilder signature = Common.concatString("-",
                po.getUserId().toString(), po.getRoleId().toString(), po.getCreatorId().toString());
        this.signature = Common.getSHA256(signature.toString());
    }

    public UserRole() {

    }


    public UserRoleRetVO createVo() {
        UserRoleRetVO userRoleRetVo = new UserRoleRetVO();
        userRoleRetVo.setId(this.id);
        userRoleRetVo.setUser(this.user.createSimpleVO());
        userRoleRetVo.setCreator(this.creator.createSimpleVO());
        userRoleRetVo.setRole(this.role.createSimpleVO());
        userRoleRetVo.setGmtcreate(this.gmtCreate);

        return userRoleRetVo;
    }


}
