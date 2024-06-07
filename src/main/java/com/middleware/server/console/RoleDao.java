package com.middleware.server.console;

import com.middleware.server.common.Common;
import com.middleware.server.common.ResponseCode;
import com.middleware.server.common.ReturnObject;
import com.middleware.server.model.BO.Privilege;
import com.middleware.server.model.BO.Role;
import com.middleware.server.model.BO.RolePrivilege;
import com.middleware.server.model.BO.User;
import com.middleware.server.model.PO.*;
import com.middleware.server.model.VO.RolePrivilegeRetVO;
import com.middleware.server.model.VO.RolePrivilegeVO;
import com.middleware.server.model.VO.RoleRetVO;
import com.middleware.server.model.VO.RoleVO;
import com.middleware.server.mapper.*;
import com.middleware.server.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class RoleDao {

    private static final Logger logger = LoggerFactory.getLogger(RoleDao.class);

    @Value("${privilegeservice.role.expiretime}")
    private long timeout;

    @Autowired
    UserRolePOMapper userRolePOMapper;

    @Autowired
    UserPOMapper userPOMapper;

    @Autowired
    RolePOMapper rolePOMapper;

    @Autowired
    PrivilegePOMapper privilegePOMapper;

    @Autowired
    RolePrivilegePOMapper rolePrivilegePOMapper;

    @Autowired
    PrivilegeDao privilegeDao;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    com.middleware.server.util.Logger logger1;
    public final Log log=new Log(this.getClass().getName(),"log");
    public void initialize() {
        //rolePrivilege表的初始化
        RolePrivilegePOExample example = new RolePrivilegePOExample();
        List<RolePrivilegePO> rolePrivilegePOS = rolePrivilegePOMapper.selectByExample(example);
        for (RolePrivilegePO rolePrivilegePO : rolePrivilegePOS) {
            RolePrivilege rolePrivilege = new RolePrivilege(rolePrivilegePO);
            rolePrivilegePO.setSignature(rolePrivilege.getSignature());
            rolePrivilegePO.setGmtModified(LocalDateTime.now());
            logger.debug("role"+rolePrivilegePO.getRoleId()+"   privilege"+rolePrivilegePO.getPrivilegeId()+"initialize signature");
            rolePrivilegePOMapper.updateByPrimaryKeySelective(rolePrivilegePO);
        }
    }

//
//    /**
//     * @param roleVo 角色的Vo对象
//     * @return Create By Gan Xinchao
//     */
//    public ReturnObject<RoleRetVO> addRole(RoleVO roleVo) {
//        ReturnObject returnObject = null;
//        try {
//            RolePO rolePo = roleVo.toRolePO();
//            rolePo.setGmtCreate(LocalDateTime.now());
//            rolePo.setGmtModified(LocalDateTime.now());
//            roleVo = new RoleVO(rolePo);
//            rolePOMapper.insert(rolePo);
//            RoleRetVO roleRetVO = roleVo.toRoleRetVO();
//            roleRetVO.setId(rolePo.getId());
//            returnObject = new ReturnObject(roleRetVO);
//        } catch (Exception e) {
//            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
////            e.printStackTrace();
//        }
//        return returnObject;
//    }

    ;
//
//    /**
//     * 删除角色
//     *
//     * @param roleId 角色ID
//     * @return Create By Gan Xinchao
//     */
//    public ReturnObject<Integer> deleteRoleByID(Long roleId) {
//        ReturnObject returnObject = null;
//        try {
//            Integer a = rolePOMapper.deleteByPrimaryKey(roleId);
//            returnObject = new ReturnObject(a);
//            logger1.writeLog(log,"deleteRoleByID","删除角色"+roleId);
//        } catch (Exception e) {
//            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
//            logger1.writeLog(log,"deleteRoleByID","删除角色"+roleId+"失败");
////            e.printStackTrace();
//        }
//        return returnObject;
//    }

    ;
//
//
//    /**
//     * 修改角色信息
//     *
//     * @param roleVO 更新角色的信息的VO对象
//     * @return 返回操作成功或失败
//     * Create By Gan Xinchao
//     */
//    public ReturnObject<Integer> updateRoleByID(RoleVO roleVO) {
//        ReturnObject returnObject = null;
//        try {
//            RolePO rolePO = roleVO.toRolePO();
//            rolePO.setId(roleVO.getId());
//            rolePO.setGmtModified(LocalDateTime.now());
//            Integer a = rolePOMapper.updateByPrimaryKey(rolePO);
//            String roleKey = "r_" + roleVO.getId();
//            //修改角色在redis的信息
//            if (redisTemplate.hasKey(roleKey)) {
//                redisTemplate.delete(roleKey);
//            }
//            returnObject = new ReturnObject(a);
//            logger1.writeLog(log,"updateRoleByID","更新角色信息"+"成功");
//        } catch (Exception e) {
//            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
//            logger1.writeLog(log,"updateRoleByID","更新角色信息"+"失败");
////            e.printStackTrace();
//        }
//        return returnObject;
//    }
//
//    ;
//
//    /**
//     * 根据id查找角色
//     *
//     * @param id 角色id
//     * @return Create By Gan Xinchao
//     */
//    public ReturnObject<RoleVO> selectRoleByID(Long id) {
//        ReturnObject returnObject = null;
//        try {
//            RolePO rolePo = rolePOMapper.selectByPrimaryKey(id);
//            RoleVO roleVO = new RoleVO(rolePo);
//            returnObject = new ReturnObject<RoleVO>(roleVO);
//            logger1.writeLog(log,"selectRoleByID","查询角色"+id+"成功");
//        } catch (Exception e) {
//            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
//            logger1.writeLog(log,"selectRoleByID","查询角色"+id+"失败");
//            e.printStackTrace();
//        }
//        return returnObject;
//    }
//
//    /**
//     * 根据角色信息查询角色
//     *
//     * @param roleVO 角色信息VO对象
//     * @return 返回查询结果List等查询信息
//     * Create By Gan Xinchao
//     */
//    public ReturnObject<List<RoleVO>> selectRoleByExample(RoleVO roleVO) {
//        ReturnObject returnObject = null;
//        try {
//            RolePOExample example = new RolePOExample();
//            RolePOExample.Criteria criteria = example.createCriteria();
//            if (roleVO.getId() != null) {
//                criteria.andIdEqualTo(roleVO.getId());
//            } else if (roleVO.getName() != null) {
//                criteria.andNameEqualTo(roleVO.getName());
//            } else {
//                if (roleVO.getCreatorid() != null) {
//                    criteria.andCreatorIdEqualTo(roleVO.getCreatorid());
//                }
//                if (roleVO.getDescription() != null) {
//                    criteria.andDescriptionEqualTo(roleVO.getDescription());
//                }
//                if (roleVO.getGmtcreate() != null) {
//                    criteria.andGmtCreateEqualTo(roleVO.getGmtcreate());
//                }
//                if (roleVO.getGmtmodified() != null) {
//                    criteria.andGmtModifiedEqualTo(roleVO.getGmtmodified());
//                }
//            }
//            List<RolePO> resultList = rolePOMapper.selectByExample(example);
//            List<RoleVO> returnList = new ArrayList<RoleVO>();
//            for (RolePO rolePO : resultList) {
//                RoleVO roleVO1 = new RoleVO(rolePO);
//                returnList.add(roleVO1);
//            }
//            returnObject = new ReturnObject(returnList);
//            logger1.writeLog(log,"selectRoleByExample","查询角色"+"成功");
//        } catch (Exception e) {
//            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
//            logger1.writeLog(log,"selectRoleByExample","查询角色失败");
////          e.printStackTrace();
//        }
//        return returnObject;
//    }
//
//
////-------------------------角色&权限部分----------------------------------------------------------------------------------
//    /**
//     * 增加角色权限
//     * @param creatorId   创建者
//     * @param roleId      角色
//     * @param privilegeId 权限
//     * @return Create By GXC 9:04
//     */
//    public ReturnObject roleAddPrivilege(Long creatorId, Long roleId, Long privilegeId) {
//        ReturnObject returnObject = null;
//        RolePO rolePO;
//        UserPO creatorPo;
//        PrivilegePO privilegePO;
//        //创建者是否存在
//        try {
//            creatorPo = userPOMapper.selectByPrimaryKey(creatorId);
//            if (creatorPo == null) {
//                returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "创建者id不存在");
//                return returnObject;
//            }
//        } catch (Exception e) {
//            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
//            return returnObject;
//        }
//        //判断角色是否存在
//        try {
//            rolePO = rolePOMapper.selectByPrimaryKey(roleId);
//            if (rolePO == null) {
//                returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "用户id不存在");
//                return returnObject;
//            }
//        } catch (Exception e) {
//            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
//            return returnObject;
//        }
//        //权限是否存在
//        try {
//            privilegePO = privilegePOMapper.selectByPrimaryKey(privilegeId);
//            if (privilegePO == null) {
//                returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "角色id不存在");
//                return returnObject;
//            }
//        } catch (Exception e) {
//            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
//            return returnObject;
//        }
//        //插入数据库
//        RolePrivilegePO rolePrivilegePO = new RolePrivilegePO();
//        rolePrivilegePO.setRoleId(roleId);
//        rolePrivilegePO.setGmtCreate(LocalDateTime.now());
//        rolePrivilegePO.setGmtModified(LocalDateTime.now());
//        rolePrivilegePO.setCreatorId(creatorId);
//        rolePrivilegePO.setPrivilegeId(privilegeId);
//        RolePrivilege rolePrivilege = new RolePrivilege(rolePrivilegePO, new Role(rolePO), new Privilege(privilegePO), new User(creatorPo));
//        rolePrivilegePO.setSignature(rolePrivilege.getSignature());
//        rolePrivilegePOMapper.insert(rolePrivilegePO);
//        String roleKey = "r_" + roleId;
//        //修改角色在redis的信息
//        if (redisTemplate.hasKey(roleKey)) {
//            redisTemplate.delete(roleKey);
//            loadRolePriv(roleId);
//        }
//        RolePrivilegeRetVO rolePrivilegeRetVO = rolePrivilege.toRetVO();
//        returnObject = new ReturnObject(rolePrivilegeRetVO);
//        return returnObject;
//    }
//
//    /**
//     * 根据id删除角色权限
//     *
//     * @param id 角色权限的唯一id
//     * @return Create By GXC
//     */
//    public ReturnObject roleDeletePrivilegeById(Long id) {
//        ReturnObject returnObject = null;
//        try {
//            Integer a = rolePrivilegePOMapper.deleteByPrimaryKey(id);
//            RolePrivilegePO rolePrivilegePO=rolePrivilegePOMapper.selectByPrimaryKey(id);
//            String roleKey = "r_" + rolePrivilegePO.getRoleId();
//            //修改角色在redis的信息
//            if (redisTemplate.hasKey(roleKey)) {
//                redisTemplate.delete(roleKey);
//                loadRolePriv(rolePrivilegePO.getRoleId());
//            }
//            returnObject = new ReturnObject(a);
//        } catch (Exception e) {
//            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
//            e.printStackTrace();
//        }
//        return returnObject;
//    }
//
//    /**
//     * 根据角色id和权限id删除该权限
//     *
//     * @param rId 角色id
//     * @param pId 权限id
//     * @return Create By GXC
//     */
//    public ReturnObject roleDeletePrivilege(Long rId, Long pId) {
//        ReturnObject returnObject = null;
//        try {
//            RolePrivilegePOExample example = new RolePrivilegePOExample();
//            RolePrivilegePOExample.Criteria criteria = example.createCriteria();
//            if (rId == null || pId == null) {
//                returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "参数缺少");
//            } else {
//                criteria.andRoleIdEqualTo(rId);
//                criteria.andPrivilegeIdEqualTo(pId);
//                List<RolePrivilegePO> resultList = rolePrivilegePOMapper.selectByExample(example);
//                if (resultList.isEmpty()) {
//                    returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "该条目不存在，请检查rId和pId是否正确");
//                } else {
//                    Long id = resultList.get(0).getId();
//                    Integer a = rolePrivilegePOMapper.deleteByPrimaryKey(id);
//                    String roleKey="r_"+rId;
//                    if (redisTemplate.hasKey(roleKey)) {
//                        redisTemplate.delete(roleKey);
//                        loadRolePriv(rId);
//                    }
//                    returnObject = new ReturnObject(a);
//                }
//            }
//        } catch (Exception e) {
//            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
//            e.printStackTrace();
//        }
//        return returnObject;
//    }
//
//    /**
//     * 根据角色权限信息查找对应角色权限元组
//     *
//     * @param rolePrivilegeVO
//     * @return Create By GXC
//     */
//    public ReturnObject selectRolePrivilege(RolePrivilegeVO rolePrivilegeVO) {
//        ReturnObject returnObject = null;
//        try {
//            RolePrivilegePOExample example = new RolePrivilegePOExample();
//            RolePrivilegePOExample.Criteria criteria = example.createCriteria();
//            if (rolePrivilegeVO.getPrivilegeid() != null) {
//                criteria.andIdEqualTo(rolePrivilegeVO.getPrivilegeid());
//            } else {
//                if (rolePrivilegeVO.getRoleid() != null) {
//                    criteria.andRoleIdEqualTo(rolePrivilegeVO.getRoleid());
//                }
//                if (rolePrivilegeVO.getPrivilegeid() != null) {
//                    criteria.andPrivilegeIdEqualTo(rolePrivilegeVO.getPrivilegeid());
//                }
//                if (rolePrivilegeVO.getCreatorid() != null) {
//                    criteria.andCreatorIdEqualTo(rolePrivilegeVO.getCreatorid());
//                }
//                if (rolePrivilegeVO.getGmtmodified() != null) {
//                    criteria.andGmtModifiedEqualTo(rolePrivilegeVO.getGmtmodified());
//                }
//                if (rolePrivilegeVO.getGmtcreate() != null) {
//                    criteria.andGmtCreateEqualTo(rolePrivilegeVO.getGmtcreate());
//                }
//            }
//            List<RolePrivilegePO> resultList = rolePrivilegePOMapper.selectByExample(example);
//            List<RolePrivilegeVO> returnList = new ArrayList<RolePrivilegeVO>();
//            for (RolePrivilegePO rolePrivilegePO : resultList) {
//                RolePrivilegeVO rolePrivilegeVO1 = new RolePrivilegeVO(rolePrivilegePO);
//                returnList.add(rolePrivilegeVO1);
//            }
//            returnObject = new ReturnObject(returnList);
//        } catch (Exception e) {
//            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
//            e.printStackTrace();
//        }
//        return returnObject;
//    }
//
//    /**
//     * 获取角色id下的所有权限的id
//     *
//     * @param id 角色id
//     * @return
//     */
//    private List<Long> getPrivIdsByRoleId(Long id) {
//        RolePrivilegePOExample example = new RolePrivilegePOExample();
//        RolePrivilegePOExample.Criteria criteria = example.createCriteria();
//        criteria.andRoleIdEqualTo(id);
//        List<RolePrivilegePO> rolePrivilegePos = rolePrivilegePOMapper.selectByExample(example);
//        List<Long> retIds = new ArrayList<>(rolePrivilegePos.size());
//        for (RolePrivilegePO po : rolePrivilegePos) {
//            StringBuilder signature = Common.concatString("-", po.getRoleId().toString(),
//                    po.getPrivilegeId().toString(), po.getCreatorId().toString());
//            String newSignature = Common.getSHA256(signature.toString());
//
//            if (newSignature.equals(po.getSignature())) {
//                retIds.add(po.getPrivilegeId());
//                logger.debug("getPrivIdsBByRoleId: roleId = " + po.getRoleId() + " privId = " + po.getPrivilegeId());
//            } else {
//                logger.info("getPrivIdsBByRoleId: Wrong Signature(auth_role_privilege): id =" + po.getId());
//            }
//        }
//        return retIds;
//    }

    public void loadRolePriv(Long id) {
        RolePrivilegePOExample example = new RolePrivilegePOExample();
        RolePrivilegePOExample.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(id);
        List<RolePrivilegePO> privs = rolePrivilegePOMapper.selectByExample(example);
        List<Long> privIds = new ArrayList<>();
        for (RolePrivilegePO privilegePO : privs) {
            privIds.add(privilegePO.getPrivilegeId());
        }
        String key = "r_" + id;
        List<String> list = new ArrayList<>();
//        for (Long pFatherId : privIds) {
//            List<String> childKey = privilegeDao.getChild(pFatherId);
//            for (String s : childKey) {
//                list.add(s);
//            }
//        }
        //去除重复部分
        //newList保存去重后的数组
        List newList = new ArrayList();
        for (String s : list) {
            if (!newList.contains(s)) {
                newList.add(s);
                redisTemplate.opsForSet().add(key, s);
            }
        }
        System.out.println(Arrays.toString(newList.toArray()));
        long randTimeout = Common.addRandomTime(this.timeout);
        redisTemplate.expire(key, randTimeout, TimeUnit.SECONDS);
    }
}
