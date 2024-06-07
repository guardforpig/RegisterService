package com.middleware.server.model.VO;


import com.middleware.server.model.PO.UserPO;

public class UserSimpleRetVo {

    private Long id;

    private String name;

    public UserSimpleRetVo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserSimpleRetVo(UserPO userPO) {
        id=userPO.getId();
        name=userPO.getUsername();
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
