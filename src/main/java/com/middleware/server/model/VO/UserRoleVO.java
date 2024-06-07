package com.middleware.server.model.VO;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UserRoleVO {
    private Long id;
    private Long roleid;
    private Long userid;
    private Long creatorid;
    private LocalDateTime gmtcreate;
    private LocalDateTime gmtmodified;

    public UserRoleVO(Long id, Long roleid, Long userid, Long creatorId, LocalDateTime gmtCreate, LocalDateTime gmtModified) {
        this.id = id;
        this.roleid = roleid;
        this.userid = userid;
        this.creatorid = creatorId;
        this.gmtcreate = gmtCreate;
        this.gmtmodified = gmtModified;
    }

    public UserRoleVO() {
    }
}
