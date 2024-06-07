package com.middleware.server.console;

import com.middleware.server.common.ReturnObject;
import com.middleware.server.console.AdminDao;
import com.middleware.server.model.VO.RoleVO;
import com.middleware.server.model.VO.UserSimpleVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public class AdminService {


    @Autowired
    AdminDao adminDao;
    @Autowired
    UserDao userDao;
//------------------------------------用户部分（包含用户×角色）-----------------------------------------------------------------
    /**
     * 管理员获取所有用户
     * @param page 页码
     * @param pageSize 一页的条目数量
     * @return
     * @author GXC
     */
    public ReturnObject<PageInfo<UserSimpleVO>> getAllUser(Integer page, Integer pageSize){
        return adminDao.getAllUser(page,pageSize);
    }

    /**
     * 管理员新增用户
     * @param roleNameList
     * @param creatorId
     * @return
     * @author GXC at 16:09 2021/7/18
     */
    public ReturnObject newUser(String userName,List<String> roleNameList, Long creatorId){
        return adminDao.newUser(userName,roleNameList,creatorId);
    }
    /**
     * 管理员查询某个用户的信息
     * @param userId 用户id
     * @return
     * @author GXC at 16:09 2021/7/18
     */
    public ReturnObject getSingleUser(Long userId){
        return adminDao.getSingleUser(userId);
    }
    /**
     * 逻辑删除某个用户（state=3）
     * @param userId 用户的id
     * @return
     * @author GXC at 17:39 2021/7/18
     */
    public ReturnObject banUser(Long userId){
        return adminDao.banUser(userId);
    }
    /**
     * 将用户的状态在正常状态切和过渡状态进行一次切换
     * @param userId 用户id
     * @return
     * @author GXC at 23:41 2021/7/18
     */
    public ReturnObject reverseUser(Long userId){
        return adminDao.reverseUser(userId);
    }
    /**
     * 管理员初始化用户密码，暂定123456
     * @param userId 用户id
     * @return
     * @author GXC at 23:41 2021/7/18
     */
    public ReturnObject resetUserPassword(Long userId) {
        return adminDao.resetUserPassword(userId);
    }

    /**
     * 管理员批量修改员工角色（已有删除，未有则添加）
     * @param userId 操作目标用户ID
     * @param roleIdList 角色列表
     * @param creatorId 创建者id
     * @return
     * @author GXC at 9:32 2021/7/19
     */
    public ReturnObject modifyUserRole(Long userId, List<Long> roleIdList,Long creatorId) {
        return adminDao.modifyUserRole(userId,roleIdList,creatorId);
    }
    //----------------------------------------角色部分---------------------------------------------------------------------------

    /**
     * 查看所有角色
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<RoleVO>> getAllRole(Integer page, Integer pageSize) {
        return adminDao.getAllRole(page,pageSize);
    }
    /**
     * 管理员修改角色描述
     * @param roleId
     * @param description
     * @return
     * @Author GXC at 17:04 2021/7/19
     */
    public ReturnObject modifyRoleDescription(Long roleId, String description,Long creatorId) {
        return adminDao.modifyRoleDescription(roleId,description,creatorId);
    }

    /**
     * 批量修改某角色的权限
     * @param roleId
     * @param privilegeIdList
     * @param creatorId
     * @return
     */
    public ReturnObject modifyRolePrivilege(Long roleId, List<Long> privilegeIdList, Long creatorId) {
        return adminDao.modifyRolePrivilege(roleId,privilegeIdList,creatorId);
    }
  //-----------------------权限部分-------------------------------------------
    public ReturnObject getPrivilege() {
        return new ReturnObject(adminDao.getPrivilege());
    }
}
