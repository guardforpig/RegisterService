package com.middleware.server.model.VO;

import com.middleware.server.model.PO.RolePO;

public class RoleSimpleRetVo {
    private Long id;

    private String name;

    public RoleSimpleRetVo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleSimpleRetVo(RolePO rolePO) {
        id=rolePO.getId();
        name=rolePO.getName();
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
}
