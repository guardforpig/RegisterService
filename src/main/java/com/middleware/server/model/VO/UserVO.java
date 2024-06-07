package com.middleware.server.model.VO;

import com.middleware.server.model.PO.UserPO;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UserVO {
    private Long id;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 出生日期
     */
    private LocalDateTime birthday;

    public UserVO() {
    }

    public UserVO(UserPO userPO) {
        this.id = userPO.getId();
        this.mobile = userPO.getMobile();
        this.username = userPO.getUsername();
        this.password = userPO.getPassword();
    }


    public UserPO getUserPo(){
        UserPO userPo = new UserPO();
        userPo.setState(0);
        userPo.setMobile(this.mobile);
        userPo.setUsername(this.username);
        userPo.setPassword(this.password);
        return userPo;
    }

    public UserRetVO getUserRetVo(){
        UserRetVO userRetVo = new UserRetVO();
        userRetVo.setBirthday(this.birthday);
        userRetVo.setGender(this.gender);
        userRetVo.setMobile(this.mobile);
        userRetVo.setEmail(this.email);
        userRetVo.setUsername(this.username);
        return userRetVo;
    }
}
