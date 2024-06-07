package com.middleware.server.model.VO;

import com.middleware.server.model.PO.PrivilegePO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PrivilegeVO {
    private Long id;
    private String name;
    private LocalDateTime gmtcreate;
    private LocalDateTime gmtmodified;
    private String url;
    private Integer requesttype;
    private String signature;

    public PrivilegeVO(PrivilegePO privilegePO){
        if(privilegePO!=null){
            id=privilegePO.getId();
            name=privilegePO.getName();
            gmtcreate=privilegePO.getGmtCreate();
            gmtmodified=privilegePO.getGmtModified();
            url=privilegePO.getUrl();
            requesttype=privilegePO.getRequestType();
            signature=privilegePO.getSignature();
        }
    }

    public PrivilegeVO(Long id, String name, LocalDateTime gmtCreate,
                       LocalDateTime gmtModified, String url,
                       Integer requestType, String signature) {
        this.id = id;
        this.name = name;
        this.gmtcreate = gmtCreate;
        this.gmtmodified = gmtModified;
        this.url = url;
        this.requesttype = requestType;
        this.signature = signature;
    }

    public PrivilegeVO() {
    }


    public PrivilegePO toPrivilegePO(){
        PrivilegePO privilegePO=new PrivilegePO();
        privilegePO.setId(id);
        privilegePO.setName(name);
        privilegePO.setGmtCreate(gmtcreate);
        privilegePO.setGmtModified(gmtmodified);
        privilegePO.setUrl(url);
        privilegePO.setRequestType(requesttype);
        privilegePO.setSignature(signature);
        return privilegePO;
    }
}
