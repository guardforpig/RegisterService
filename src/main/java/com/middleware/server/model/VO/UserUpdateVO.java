package com.middleware.server.model.VO;

import com.middleware.server.model.PO.UserPO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 */
@Data
public class UserUpdateVO {
    private Long id;
    private String username;
    private String mobile;
    private String email;
    private LocalDateTime gmtmodified;
    private Integer state;
    private Long staffid;
    private Integer gender;
    private LocalDateTime birthday;
    public UserPO toUserPo(){
        UserPO userPO=new UserPO();
        userPO.setId(id);
        userPO.setUsername(username);
        userPO.setMobile(mobile);
        userPO.setGmtModified(gmtmodified);
        userPO.setState(state);
        return userPO;
    }
}
