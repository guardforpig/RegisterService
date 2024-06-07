package com.middleware.server.console;

import com.middleware.server.common.ReturnObject;
import com.middleware.server.model.VO.UserSelectVO;
import com.middleware.server.model.VO.UserUpdateVO;
import com.middleware.server.model.VO.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    /**
     * @author GXC
     * @param username 用户名
     * @param password 密码
     * @return returnObject
     * time：2021年7月6日16:47:31
     */
    public ReturnObject userLogin(String username,String password){
        return userDao.userLogin(username,password);
    }

    public ReturnObject userLogout(Long userId){
        return userDao.userLogout(userId);
    }
//-----------------------------------------------用户与角色部分------------------------------------------------------------

    /**
     * 新增用户角色
     * @param creatorId
     * @param userId
     * @param roleId
     * @return
     */
    public ReturnObject userAddRole(Long creatorId,Long userId,Long roleId) {
        return userDao.userAddRole(creatorId,userId,roleId);
    }
    /**
     * 删除某个用户的某个角色
     * @param uid 用户id
     * @param rid 角色id
     * @return
     * Create By GXC
     */
    public ReturnObject deleteUserRole(Long uid,Long rid){return userDao.deleteUserRole(uid,rid);}
    /**
     * 查询某个id用户所拥有的角色信息
     * @param tid 用户的id
     * @return 该用户角色信息
     * Create By GXC
     */
    public ReturnObject selectUserRoleByuId(Long tid){
        return userDao.selectUserRoleByuId(tid);
    }

}
