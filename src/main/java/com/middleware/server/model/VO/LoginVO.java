package com.middleware.server.model.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel
@Data
public class LoginVO {
    @NotBlank(message = "必须输入用户名")
    @ApiModelProperty(name = "用户名")
    private String username;

    @NotBlank(message = "必须输入密码")
    @ApiModelProperty(name = "密码")
    private String password;

    public LoginVO(String name, String pwd) {
        username=name;
        password=pwd;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
