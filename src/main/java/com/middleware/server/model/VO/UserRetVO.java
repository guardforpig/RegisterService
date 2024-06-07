package com.middleware.server.model.VO;

import com.middleware.server.model.PO.UserPO;

import java.time.LocalDateTime;

public class UserRetVO {
    private Long id;
    private String username;
    private String mobile;
    private String email;
    private Integer gender;
    private LocalDateTime birthday;
    private Integer state;
    private LocalDateTime gmtcreate;
    private LocalDateTime gmtmodify;

    public UserRetVO() {
    }

    public UserRetVO(UserPO userPO) {
        this.id = userPO.getId();
        this.username = userPO.getUsername();
        this.mobile = userPO.getMobile();
        this.state = userPO.getState();
        this.gmtcreate = userPO.getGmtCreate();
        this.gmtmodify = userPO.getGmtModified();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public LocalDateTime getGmtCreate() {
        return gmtcreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtcreate = gmtCreate;
    }

    public LocalDateTime getGmtModify() {
        return gmtmodify;
    }

    public void setGmtModify(LocalDateTime gmtModify) {
        this.gmtmodify = gmtModify;
    }
}
