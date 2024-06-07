package com.middleware.server.model.BO;


import com.middleware.server.model.PO.RolePO;
import com.middleware.server.model.VO.RoleSimpleRetVo;

import java.time.LocalDateTime;

public class Role {
    private Long id;
    private String name;
    private Long creatorId;
    private Long departId;
    private String desciription;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Role() {
    }

    public Role(RolePO po) {
        this.id = po.getId();
        this.name = po.getName();
        this.creatorId = po.getCreatorId();
        this.desciription = po.getDescription();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getDepartId() {
        return departId;
    }

    public void setDepartId(Long departId) {
        this.departId = departId;
    }

    public String getDesciription() {
        return desciription;
    }

    public void setDesciription(String desciription) {
        this.desciription = desciription;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public RoleSimpleRetVo createSimpleVO() {
        RoleSimpleRetVo roleSimpleRetVo = new RoleSimpleRetVo(this.id,this.name);
        return roleSimpleRetVo;
    }
}
