package com.middleware.server.model.VO;

import com.middleware.server.model.VOObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserSimpleVO implements VOObject {
    @ApiModelProperty(name = "用户id")
    Long userid;
    @ApiModelProperty(name = "用户状态" +
            "0-初始化状态（未分配员工，未设定角色，未修改默认密码）" +
            "1-已分配员工" +
            "2-过渡期（权限锁定，只能访问相关页面，适用于离职程序、试用期等临时状态）" +
            "3-作废")
    Integer state;
    @ApiModelProperty(name = "用户名")
    String userName;
    @ApiModelProperty(name = "用户所拥有的角色")
    List<RoleSimpleVO> rolelist;

    public UserSimpleVO(Long id, Integer state, String name, List<RoleSimpleVO> roleList) {
        this.userid=id;
        this.state=state;
        this.userName=name;
        this.rolelist=roleList;
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
