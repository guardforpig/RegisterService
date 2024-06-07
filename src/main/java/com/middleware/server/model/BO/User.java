package com.middleware.server.model.BO;

import com.middleware.server.model.PO.UserPO;
import com.middleware.server.model.VO.UserRetVO;
import com.middleware.server.model.VO.UserSimpleRetVo;

import java.time.LocalDateTime;

public class User {

    /**
     *
     */
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

    public User() {
    }

    public User(UserPO userPO) {
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public UserSimpleRetVo createSimpleVO() {
        return new UserSimpleRetVo(this.id,this.username);
    }
}
