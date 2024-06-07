package com.middleware.server.model.VO;

import lombok.Data;

@Data
public class PrivilegeSimpleRetVO {
    private Long id;
    private  String url;
    private Integer requesttype;

    public PrivilegeSimpleRetVO(Long id, String url, Integer requestType) {
        this.id = id;
        this.url = url;
        this.requesttype = requestType;
    }
}
