package com.middleware.server.model.BO;

import com.alibaba.fastjson.JSON;
import com.middleware.server.model.PO.PrivilegePO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivilegeBO {
    private Long id;
    private String name;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private String url;
    private Integer requestType;
    private String signature;
    private List<Long> parent;

    public PrivilegeBO(PrivilegePO po){
        this.id=po.getId();
        this.name=po.getName();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
        this.url=po.getUrl();
        this.requestType=po.getRequestType();
        this.signature=po.getSignature();
        this.parent= JSON.parseArray(po.getParent(),Long.class);
    }
}
