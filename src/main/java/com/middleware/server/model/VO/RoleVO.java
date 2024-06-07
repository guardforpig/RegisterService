package com.middleware.server.model.VO;

import com.middleware.server.model.PO.RolePO;
import com.middleware.server.model.VOObject;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class RoleVO implements VOObject {
    private Long id;
    private String name;
    private Long creatorid;
    private String description;
    private LocalDateTime gmtcreate;
    private LocalDateTime gmtmodified;

    /**
     * 用来类型转换的
     * @return RolePO 返回一个本对象同属性PO对象
     * Create By Gan Xinchao 2021/07/07 16:08
     */
    public RolePO toRolePO(){
        RolePO rolePO=new RolePO();
        rolePO.setName(this.getName());
        rolePO.setCreatorId(this.getCreatorid());
        rolePO.setDescription(this.getDescription());
        rolePO.setGmtCreate(this.getGmtcreate());
        rolePO.setGmtModified(this.getGmtmodified());
        return rolePO;
    }

    /**
     * 用来类型转换的
     * @return RoleRetVO 返回一个本对象同属性RetVO对象
     * Create By Gan Xinchao 2021/07/07 16:08
     */
    public RoleRetVO toRoleRetVO(){
        RoleRetVO roleRetVO=new RoleRetVO(this.getName(),
                this.getCreatorid(),
                this.getDescription(),
                this.getGmtcreate(),
                this.getGmtmodified());
        return roleRetVO;
    }

    public RoleVO(RolePO rolePO) {
        id=rolePO.getId();
        name=rolePO.getName();
        creatorid=rolePO.getCreatorId();
        description=rolePO.getDescription();
        gmtcreate=rolePO.getGmtCreate();
        gmtmodified=rolePO.getGmtModified();
    }

    public RoleVO(Long id, String name, Long creatorId, String description, LocalDateTime gmtCreate, LocalDateTime gmtModified) {
        this.id = id;
        this.name = name;
        this.creatorid = creatorId;
        this.description = description;
        this.gmtcreate = gmtCreate;
        this.gmtmodified = gmtModified;
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
