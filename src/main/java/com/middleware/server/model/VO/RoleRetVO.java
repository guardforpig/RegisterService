package com.middleware.server.model.VO;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class RoleRetVO {
    private Long id;
    private String name;
    private Long creatorid;
    private String description;
    private LocalDateTime gmtcreate;
    private LocalDateTime gmtmodified;

    public RoleRetVO(String name, Long creatorId, String description, LocalDateTime gmtCreate, LocalDateTime gmtModified) {
        this.name = name;
        this.creatorid = creatorId;
        this.description = description;
        this.gmtcreate = gmtCreate;
        this.gmtmodified = gmtModified;
    }
}
