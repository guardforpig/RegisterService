package com.middleware.server.model.VO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminRoleVO {
    private String name;
    private Long creatorid;
    private String description;
    private LocalDateTime gmtcreate;
    private LocalDateTime gmtmodified;
}
