package com.middleware.server.model.VO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Gan XinChao
 * create at 2021/07/06 20:39
 */
@Data
public class UserSelectVO {
    private Long id;
    private String username;
    private String mobile;
    private LocalDateTime gmtcreate;
    private String email;
    private LocalDateTime gmtmodified;
    private Integer state;
    private Integer gender;
    private LocalDateTime birthday;
    private Integer disable;

    public UserSelectVO(Long id, String username, String mobile,
                        LocalDateTime gmtCreate, String email,
                        LocalDateTime gmtModified, Integer state,
                        Integer gender, LocalDateTime birthday,
                        Integer disable) {
        this.id = id;
        this.username = username;
        this.mobile = mobile;
        this.gmtcreate = gmtCreate;
        this.email = email;
        this.gmtmodified = gmtModified;
        this.state = state;
        this.gender = gender;
        this.birthday = birthday;
        this.disable = disable;
    }
}
