package com.middleware.server.model.VO;

import com.middleware.server.model.BO.PrivilegeBO;
import com.middleware.server.model.VOObject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class PrivilegeRouteVO implements VOObject {
    String name;
    List<PrivilegeRouteVO> children;
    List<String> actions;

    public PrivilegeRouteVO() {
    }

    public PrivilegeRouteVO(String name) {
        this.name = name;
        children=new ArrayList<>();
        actions=new ArrayList<>();
    }

    public PrivilegeRouteVO(PrivilegeBO bo){
        this.name = bo.getName();
        children=new ArrayList<>();
        actions=new ArrayList<>();
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
