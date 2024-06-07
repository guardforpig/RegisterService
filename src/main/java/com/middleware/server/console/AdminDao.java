package com.middleware.server.console;

import com.middleware.server.common.ResponseCode;
import com.middleware.server.common.ReturnObject;
import com.middleware.server.mapper.*;
import com.middleware.server.model.BO.PrivilegeBO;
import com.middleware.server.model.BO.UserRole;
import com.middleware.server.model.PO.*;
import com.middleware.server.model.VO.*;
import com.middleware.server.util.Log;
import com.middleware.server.util.Logger;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Repository
@Slf4j
public class AdminDao {

    @Autowired
    UserPOMapper userPOMapper;
    @Autowired
    UserRolePOMapper userRolePOMapper;
    @Autowired
    RolePOMapper rolePOMapper;
    @Autowired
    RolePrivilegePOMapper rolePrivilegePOMapper;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    private PrivilegePOMapper privilegePOMapper;
    @Autowired
    Logger logger;

    public final Log log=new Log(this.getClass().getName(),"log");
//------------------------------------用户部分（包含用户×角色）-----------------------------------------------------------------
    /**
     * 管理员查看是所有用户
     * @param page 页码
     * @param pageSize 一页条目数量
     * @return
     * @author GXC
     */
    public ReturnObject<PageInfo<UserSimpleVO>> getAllUser(Integer page, Integer pageSize){
        ReturnObject returnObject = null;
        try{
            List<UserSimpleVO> allUserList=new ArrayList<UserSimpleVO>();
            //得到所有的用户
            UserPOExample example=new UserPOExample();
            UserPOExample.Criteria criteria=example.createCriteria();
            PageHelper.startPage(page,pageSize);
            List<UserPO> userList= userPOMapper.selectByExample(example);

            for(UserPO userPO:userList){
                Long userId=userPO.getId();
                //得到用户的所有角色的列表
                UserRolePOExample example1=new UserRolePOExample();
                UserRolePOExample.Criteria criteria1=example1.createCriteria();
                criteria1.andUserIdEqualTo(userId);
                List<UserRolePO> userRoleList=userRolePOMapper.selectByExample(example1);
                List<RoleSimpleVO> roleList=new ArrayList<RoleSimpleVO>();
                if(userRoleList!=null||!userRoleList.isEmpty()){
                    for(UserRolePO userRolePO:userRoleList){
                        Long roleid=userRolePO.getRoleId();
                        RolePO rolePO=rolePOMapper.selectByPrimaryKey(roleid);
                        RoleSimpleVO roleSimpleVO=new RoleSimpleVO(rolePO.getName(),rolePO.getDescription());
                        roleList.add(roleSimpleVO);
                    }
                }
                UserSimpleVO userSimpleVO=new UserSimpleVO(userPO.getId(),userPO.getState(),userPO.getUsername(),roleList);
                allUserList.add(userSimpleVO);
            }
            PageInfo<UserSimpleVO> announcementRetVOPageInfo=new PageInfo<UserSimpleVO>(allUserList);
            announcementRetVOPageInfo.setPages(PageInfo.of(userList).getPages());
            announcementRetVOPageInfo.setTotal(PageInfo.of(userList).getTotal());
            announcementRetVOPageInfo.setPageNum(page);
            announcementRetVOPageInfo.setPageSize(pageSize);
            logger.writeLog(log,"getAllUser","成功查询所有用户！");
            return new ReturnObject(announcementRetVOPageInfo);
        }catch (Exception e){
//            e.printStackTrace();
            logger.writeLog(log,"getAllUser","查询所有用户发生异常");
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 新建用户
     * @param roleNameList 角色名集合
     * @param creatorId 创建者id
     * @return
     * @author GXC at 11:17 2021/7/18
     */
    public ReturnObject newUser(String userName,List<String> roleNameList,Long creatorId){
        ReturnObject returnObject=null;
        try{
            //新建user
            UserPO userPO=new UserPO();
            userPO.setUsername(userName);
            userPO.setState(1);
            userPO.setGmtCreate(LocalDateTime.now());
            userPOMapper.insert(userPO);
            Long userId=userPO.getId();
            //查找role的id,并且新增userrole元组
            List<Long> roleIdList=new ArrayList<Long>();
            UserRolePO userRolePO=new UserRolePO();
            userRolePO.setUserId(userId);
            userRolePO.setCreatorId(creatorId);
            userRolePO.setGmtCreate(LocalDateTime.now());
            for(String roleName:roleNameList){
                RolePOExample rolePOExample=new RolePOExample();
                RolePOExample.Criteria criteria=rolePOExample.createCriteria();
                if(roleName!=null){
                    criteria.andNameEqualTo(roleName);
                    List<RolePO>roles=rolePOMapper.selectByExample(rolePOExample);
                    userRolePO.setId(null);
                    userRolePO.setRoleId(roles.get(0).getId());
                    userRolePOMapper.insert(userRolePO);
                }
            }
            logger.writeLog(log,"newUser","新建用户成功！");
            returnObject=new ReturnObject();
        }catch (Exception e){
            logger.writeLog(log,"newUser","新建用户失败，请检查数据是否有误！");
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
            //e.printStackTrace();
        }
        return returnObject;
    }

    /**
     * 管理员查询某个用户的信息
     * @param userId 用户id
     * @return
     * @author GXC at 16:09 2021/7/18
     */
    public ReturnObject getSingleUser(Long userId){
        ReturnObject returnObject=null;
        try{
            UserWholeVO userWholeVO=null;
            UserPO userPO=userPOMapper.selectByPrimaryKey(userId);
            //查userrole表并得到所有role的simpleVO对象
            UserRolePOExample userRolePOExample=new UserRolePOExample();
            UserRolePOExample.Criteria criteria=userRolePOExample.createCriteria();
            criteria.andUserIdEqualTo(userId);
            List<UserRolePO> userRolePOList=userRolePOMapper.selectByExample(userRolePOExample);
            List<RoleSimpleVO> roleSimpleVOList=new ArrayList<RoleSimpleVO>();
            RolePO rolePO=null;
            RoleSimpleVO roleSimpleVO=null;
            for(UserRolePO userRolePO:userRolePOList){
                rolePO=rolePOMapper.selectByPrimaryKey(userRolePO.getRoleId());
                roleSimpleVO=new RoleSimpleVO(rolePO.getName(),rolePO.getDescription());
                roleSimpleVOList.add(roleSimpleVO);
            }
            String A=null; //暂时充当LastLogin，avatar
            userWholeVO=new UserWholeVO(userId,userPO.getUsername(),userPO.getState(),roleSimpleVOList,A);
            logger.writeLog(log,"getSingleUser","管理员查看"+userId+"的完整信息！");
            returnObject=new ReturnObject(userWholeVO);
        }catch(Exception e){
            logger.writeLog(log,"getSingleUser","管理员查看某个用户的完整信息失败，请检查userid是否存在！");
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
        }
        return returnObject;
    }


    /**
     * 逻辑删除某个用户（state=3）
     * @param userId 用户的id
     * @return
     * @author GXC at 17:39 2021/7/18
     */
    public ReturnObject banUser(Long userId){
        ReturnObject returnObject=null;
        try{
            UserPO userPO=new UserPO();
            userPO.setId(userId);
            userPO.setState(3);
            userPOMapper.updateByPrimaryKeySelective(userPO);
            logger.writeLog(log,"banUser","逻辑删除用户"+userId);
            returnObject=new ReturnObject();
        }catch (Exception e){
            logger.writeLog(log,"banUser","逻辑删除用户失败，请检查userId"+userId+"是否正确");
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
        }
        return returnObject;
    }
    /**
     * 将用户的状态在正常状态切和过渡状态进行一次切换
     * @param userId 用户id
     * @return
     * @author GXC at 23:41 2021/7/18
     */
    public ReturnObject reverseUser(Long userId) {
        ReturnObject returnObject=null;
        try{
            UserPO userPO=userPOMapper.selectByPrimaryKey(userId);
            if(userPO.getState()!=3){
                if(userPO.getState()==1){
                    userPO.setState(2);
                }
                else  userPO.setState(1);
                userPO.setGmtModified(LocalDateTime.now());
                userPOMapper.updateByPrimaryKeySelective(userPO);
            }
            logger.writeLog(log,"reverseUser","改变用户"+userId+"状态成功");
            returnObject=new ReturnObject();
        }catch (Exception e){
            logger.writeLog(log,"reverseUser","改变用户"+userId+"状态失败！");
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
        }
        return returnObject;
    }
    /**
     * 管理员初始化用户密码，暂定123456
     * @param userId 用户id
     * @return
     * @author GXC at 23:41 2021/7/18
     */
    public ReturnObject resetUserPassword(Long userId){
        ReturnObject returnObject=null;
        try{
            UserPO userPO=new UserPO();
            userPO.setId(userId);
            userPO.setPassword("123456");
            userPO.setGmtModified(LocalDateTime.now());
            userPOMapper.updateByPrimaryKeySelective(userPO);
            logger.writeLog(log,"resetUserPassword","重置用户"+userId+"密码为123456");
            returnObject=new ReturnObject();
        }catch (Exception e){
            logger.writeLog(log,"resetUserPassword","重置用户"+userId+"密码失败，检查用户id");
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
        }
        return returnObject;
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
        ReturnObject returnObject=null;
        try{
            //遍历List，在userrole表中搜索，有则删除，无则添加
            UserRolePOExample example=null;
            if(roleIdList!=null)
            for(Long roleId:roleIdList){
                if(roleId!=null){
                    example=new UserRolePOExample();
                    UserRolePOExample.Criteria criteria=example.createCriteria();
                    criteria.andUserIdEqualTo(userId).andRoleIdEqualTo(roleId);
                    List<UserRolePO> userRolePOList=userRolePOMapper.selectByExample(example);
                    UserRolePO userRolePO=null;
                    if(userRolePOList.isEmpty()){
                        System.out.println("create"+roleId);
                        userRolePO=new UserRolePO();
                        userRolePO.setUserId(userId);
                        userRolePO.setRoleId(roleId);
                        userRolePO.setCreatorId(creatorId);
                        userRolePO.setGmtModified(LocalDateTime.now());
                        UserRole userRole=new UserRole(userRolePO);
                        userRolePO.setSignature(userRole.getSignature());
                        userRolePOMapper.insertSelective(userRolePO);
                    }else{
                        for(UserRolePO userRolePO1:userRolePOList){
                            Long id=userRolePO1.getId();
                            userRolePOMapper.deleteByPrimaryKey(id);
                        }
                    }
                    String userKey="u_"+userId;
                    if(redisTemplate.hasKey(userKey)){
                        redisTemplate.delete(userKey);
                    }
                }
            }
            returnObject=new ReturnObject();
        }catch (Exception e){
            logger.writeLog(log,"modifyUserRole","批量修改用户"+userId+"的角色失败，请检查角色id和userid是否有误");
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
//            e.printStackTrace();
        }
        logger.writeLog(log,"modifyUserRole","批量修改用户"+userId+"的角色");
        return returnObject;
    }
//----------------------------------------角色部分---------------------------------------------------------------------------
    public ReturnObject<PageInfo<RoleVO>> getAllRole(Integer page, Integer pageSize) {
        ReturnObject returnObject = null;
        try{
            //创建一个类包含role的全部信息，和权限的部分信息VO的List
            //查找该页的role，到list里，遍历list找出所含的权限
            //最后返回类的List的Reobj
            RolePOExample example=new RolePOExample();
            RolePOExample.Criteria criteria=example.createCriteria();
            PageHelper.startPage(page,pageSize);
            List<RolePO> resultList=rolePOMapper.selectByExample(example);
            List<RoleVO> returnList=new ArrayList<RoleVO>();
            for(RolePO rolePO:resultList){
                RoleVO roleVO=new RoleVO(rolePO);
                returnList.add(roleVO);
            }
            PageInfo<RoleVO> announcementRetVOPageInfo=new PageInfo<RoleVO>(returnList);
            announcementRetVOPageInfo.setPages(PageInfo.of(returnList).getPages());
            announcementRetVOPageInfo.setTotal(PageInfo.of(returnList).getTotal());
            announcementRetVOPageInfo.setPageNum(page);
            announcementRetVOPageInfo.setPageSize(pageSize);
            returnObject=new ReturnObject(announcementRetVOPageInfo);
        }catch (Exception e){
            //e.printStackTrace();
            logger.writeLog(log,"getAllRole","查看所有角色失败");
            returnObject= new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        }
        logger.writeLog(log,"getAllRole","查看所有角色成功");
        return returnObject;
    }
    /**
     * 管理员修改角色描述
     * @param roleId
     * @param description
     * @return
     * @Author GXC at 17:04 2021/7/19
     */
    public ReturnObject modifyRoleDescription(Long roleId, String description,Long creatorId) {
        ReturnObject returnObject = null;
        try{
            RolePO rolePO=new RolePO();
            rolePO.setId(roleId);
            rolePO.setDescription(description);
            rolePO.setCreatorId(creatorId);
            rolePOMapper.updateByPrimaryKeySelective(rolePO);
            returnObject=new ReturnObject();
        }catch (Exception e){
            //e.printStackTrace();
            logger.writeLog(log,"modifyRoleDescription","修改角色"+roleId+"失败，请检查roleId是否有误");
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
        }
        return  returnObject;
    }

    /**
     *
     * @return
     */
    public List<PrivilegeRouteVO> getPrivilege() {
        PrivilegePOExample example = new PrivilegePOExample();
        PrivilegePOExample.Criteria criteria = example.createCriteria();
        List<PrivilegePO> pos = null;
        try {
            pos = privilegePOMapper.selectByExample(example);
        } catch (Exception e) {
            e.printStackTrace();
            logger.writeLog(log,"getPrivilege","查看所有权限异常");
        }
        List<PrivilegeBO> bos = new ArrayList<>();
        for (PrivilegePO po : pos) {
            bos.add(new PrivilegeBO(po));
        }
        try{
            bos.sort(new Comparator<PrivilegeBO>() {
                @Override
                public int compare(PrivilegeBO o1, PrivilegeBO o2) {
                    return o1.getParent().get(0).intValue() - o2.getParent().get(0).intValue();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        int size=pos.get(pos.size()-1).getId().intValue()+1;
        PrivilegeRouteVO[] routes = new PrivilegeRouteVO[size];

        routes[1] = new PrivilegeRouteVO(bos.get(0));
        try {
            for (PrivilegeBO bo : bos) {
                int id = bo.getId().intValue();
                int parent = bo.getParent().get(0).intValue();
                if (parent == 0) {
                    continue;
                }
                if (routes[id] == null) {
                    routes[id] = new PrivilegeRouteVO(bo);
                }
                routes[parent].getChildren().add(routes[id]);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        /*log.info("actions");
        example.clear();
        criteria = example.createCriteria();
        criteria.andRequestTypeEqualTo(10);
        pos.clear();
        try {
            pos = privilegePOMapper.selectByExample(example);
        } catch (Exception e) {
            log.info("error pos");
        }

        bos.clear();
        for (PrivilegePO po : pos) {
            bos.add(new PrivilegeBO(po));
        }
        for (PrivilegeBO bo : bos) {
            int parent = bo.getParent().get(0).intValue();
            routes[parent].getActions().add(bo.getUrl());
        }*/
        return routes[1].getChildren();
    }
    /**
     * 批量修改某角色的权限（无增、有删
     * @param roleId
     * @param privilegeIdList
     * @param creatorId
     * @return
     */
    public ReturnObject modifyRolePrivilege(Long roleId, List<Long> privilegeIdList, Long creatorId) {
        ReturnObject returnObject=null;
        try{
            RolePrivilegePOExample example=null;
            for(Long privilegeId:privilegeIdList){
                example=new RolePrivilegePOExample();
                RolePrivilegePOExample.Criteria criteria=example.createCriteria();
                criteria.andRoleIdEqualTo(roleId).andPrivilegeIdEqualTo(privilegeId);
                List<RolePrivilegePO> rolePrivilegePOList=rolePrivilegePOMapper.selectByExample(example);
                if(rolePrivilegePOList.isEmpty()){
                    RolePrivilegePO rolePrivilegePO=new RolePrivilegePO();
                    rolePrivilegePO.setRoleId(roleId);
                    rolePrivilegePO.setGmtCreate(LocalDateTime.now());
                    rolePrivilegePO.setPrivilegeId(privilegeId);
                    rolePrivilegePO.setCreatorId(creatorId);
                    rolePrivilegePOMapper.insertSelective(rolePrivilegePO);
                }else{
                    for(RolePrivilegePO rolePrivilegePO:rolePrivilegePOList){
                        Long id=rolePrivilegePO.getId();
                        rolePrivilegePOMapper.deleteByPrimaryKey(id);
                    }
                }
                String roleKey="r_"+roleId;
                if(redisTemplate.hasKey(roleKey)){
                    redisTemplate.delete(roleKey);
                }
            }
            returnObject=new ReturnObject();
        }catch (Exception e){
            logger.writeLog(log,"modifyRolePrivilege","批量修改角色"+roleId+"的权限失败");
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
        }
        logger.writeLog(log,"modifyRolePrivilege","批量修改角色"+roleId+"的权限成功");
        return returnObject;
    }
}
