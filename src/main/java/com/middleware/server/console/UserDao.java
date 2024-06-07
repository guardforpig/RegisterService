package com.middleware.server.console;

import com.middleware.server.common.Common;
import com.middleware.server.common.ResponseCode;
import com.middleware.server.common.ReturnObject;
import com.middleware.server.common.TokenJwt;
import com.middleware.server.mapper.RolePOMapper;
import com.middleware.server.mapper.UserPOMapper;
import com.middleware.server.mapper.UserRolePOMapper;
import com.middleware.server.model.BO.Role;
import com.middleware.server.model.BO.User;
import com.middleware.server.model.BO.UserRole;
import com.middleware.server.model.PO.*;
import com.middleware.server.model.VO.*;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Mapper
@Repository
public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    @Value("${privilegeservice.user.expiretime}")
    private long timeout;
    Map<String, String> tokenMap = new HashMap<>();

    @Autowired
    UserPOMapper userPOMapper;

    @Autowired
    UserRolePOMapper userRolePOMapper;

    @Autowired
    RolePOMapper rolePOMapper;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    RoleDao roleDao;

    /**
     * 初始化用户角色，为记录加签名
     * @author GXC
     */
    public void initialize() {
        UserRolePOExample example = new UserRolePOExample();
        List<UserRolePO> userRolePOS = userRolePOMapper.selectByExample(example);
        for (UserRolePO po : userRolePOS) {
            UserRole userRole=null;
            if(po.getCreatorId()!=null)   userRole = new UserRole(po);
            else continue;
            po.setGmtModified(LocalDateTime.now());
            po.setSignature(userRole.getSignature());
            logger.debug("user"+po.getUserId()+"   role"+po.getRoleId()+"initialize signature");
            userRolePOMapper.updateByPrimaryKeySelective(po);
        }
    }
    /**
     * @param username 用户名
     * @param password 密码
     * @return returnObject
     * time：2021年7月6日16:47:31
     * @author GXC
     */
    public ReturnObject<String> userLogin(String username, String password) {
        ReturnObject returnObject = null;
        UserPOExample userPOExample = new UserPOExample();
        UserPOExample.Criteria criteria = userPOExample.createCriteria();
        System.out.println(username);
        criteria.andUsernameEqualTo(username);
        criteria.andPasswordEqualTo(password);
        List<UserPO> userPOList = userPOMapper.selectByExample(userPOExample);
        //用户密码不正确
        if (userPOList.isEmpty()) {
            logger.debug("用户名密码不正确");
            returnObject = new ReturnObject(ResponseCode.AUTH_INVALID_ACCOUNT);
        } else {
            for (UserPO userPO : userPOList) {
                //重复登录判断-----目前没有
                TokenJwt tokenJwt = new TokenJwt();
                int expireTime = 60 * 5 * 1000;
                String token = tokenJwt.creatToken(userPO.getId(), expireTime);
                //判断token是否存在
                String userKey = "u_login_" + userPO.getId().toString();
                //过期时间和token一样,存到redis
                redisTemplate.opsForValue().set(userKey, token, expireTime, TimeUnit.SECONDS);
                //查看用户的权限是否已经到redis，如果没有则放到redis,有则重新设置过期时间
                String userPrivKey = "u_" + userPO.getId();
                if (!redisTemplate.hasKey(userPrivKey)) {
                    loadAllPrivilege(userPO.getId());
                } else {
                    long randTimeout = Common.addRandomTime(timeout);
                    redisTemplate.expire(userPrivKey, randTimeout, TimeUnit.SECONDS);
                }
                TokenRetVO tokenRetVO = new TokenRetVO();
                tokenRetVO.setToken(token);
                returnObject = new ReturnObject(tokenRetVO);
            }
        }
        return returnObject;
    }
//--------------------------用户&角色部分----------------------------------------------------------------------

    /**
     * @param creatorId 创建者id
     * @param userId    用户id
     * @param roleId    角色id
     * @return
     * @author GXC
     */
    public ReturnObject userAddRole(Long creatorId, Long userId, Long roleId) {
        ReturnObject returnObject = null;
        UserPO userPO;
        UserPO creatorPo;
        RolePO rolePO;
        //先判断用户是否存在
        //1.创建者是否存在
        try {
            logger.debug("userAddRole查询创建者userId：" + creatorId);
            creatorPo = userPOMapper.selectByPrimaryKey(creatorId);
            if (creatorPo == null) {
                logger.debug("userAddRole查询创建者userId：" + creatorId + "用户不存在");
                returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "创建者id不存在");
                return returnObject;
            }
        } catch (Exception e) {
            logger.debug("userAddRole查询创建者userId：" + creatorId + "数据库错误");
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
            return returnObject;
        }
        //2.判断被创建者是否存在
        try {
            logger.debug("userAddRole查询被授权者userId：" + creatorId);
            userPO = userPOMapper.selectByPrimaryKey(userId);
            if (userPO == null) {
                logger.debug("userAddRole查询被授权者userId：" + creatorId + "用户不存在");
                returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "用户id不存在");
                return returnObject;
            }
        } catch (Exception e) {
            logger.debug("userAddRole查询被授权者userId：" + creatorId + "数据库错误");
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
            return returnObject;
        }
        //3.角色是否存在
        try {
            logger.debug("userAddRole查询角色roleId：" + roleId);
            rolePO = rolePOMapper.selectByPrimaryKey(roleId);
            if (rolePO == null) {
                logger.debug("userAddRole查询角色roleId：" + roleId + "不存在");
                returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, "角色id不存在");
                return returnObject;
            }
        } catch (Exception e) {
            logger.debug("userAddRole查询角色roleId：" + roleId + "数据库错误");
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
            return returnObject;
        }
        //4.判断是否重复
        UserRolePOExample userRolePOExample = new UserRolePOExample();
        UserRolePOExample.Criteria criteria = userRolePOExample.createCriteria();
        criteria.andCreatorIdEqualTo(creatorId);
        criteria.andUserIdEqualTo(userId);
        criteria.andRoleIdEqualTo(roleId);
        try {
            logger.debug("userAddRole查询用户角色表userId：" + userId + ",roleId:" + roleId);
            List<UserRolePO> userRolePOS = userRolePOMapper.selectByExample(userRolePOExample);
            if (userRolePOS.size() > 0) {
                logger.debug("userAddRole查询用户角色表userId：" + userId + ",roleId:" + roleId + "重复添加");
                returnObject = new ReturnObject(ResponseCode.USER_ROLE_REGISTERED);
                return returnObject;
            }
        } catch (Exception e) {
            logger.debug("userAddRole查询用户角色表userId：" + userId + ",roleId:" + roleId + "数据库错误");
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
            return returnObject;
        }
        //5.插入数据库
        UserRolePO userRolePO = new UserRolePO();
        userRolePO.setRoleId(roleId);
        userRolePO.setGmtCreate(LocalDateTime.now());
        userRolePO.setGmtModified(LocalDateTime.now());
        userRolePO.setCreatorId(creatorId);
        userRolePO.setUserId(userId);
        UserRole userRole = new UserRole(userRolePO, new User(creatorPo), new Role(rolePO), new User(userPO));
        userRolePO.setSignature(userRole.getSignature());
        try {
            //清空用户redis
            String userKey = "u_" + userPO.getId();
            logger.debug("userAddRole用户的角色修改，清空redis：key：" + userKey);
            redisTemplate.delete(userKey);
            logger.debug("userAddRole用户的角色修改插入数据库：userId："+userId+",roleId"+roleId);
            userRolePOMapper.insertSelective(userRolePO);
        } catch (Exception e) {
            logger.debug("userAddRole用户的角色修改插入数据库失败：userId："+userId+",roleId"+roleId);
            e.printStackTrace();
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "服务器错误");
            return returnObject;
        }
        UserRoleRetVO userRoleRetVO = userRole.createVo();
//        loadAllPrivilege(userId);
        returnObject = new ReturnObject(userRoleRetVO);
        return returnObject;
    }


    /**
     * 通过用户和角色id删除用户角色
     * @param uId 用户id
     * @param rid 角色id
     * @return
     * Create By GXC
     */
    public ReturnObject deleteUserRole(Long uId,Long rid){
        ReturnObject returnObject=null;
        try{
            UserRolePOExample example=new UserRolePOExample();
            UserRolePOExample.Criteria criteria= example.createCriteria();
            criteria.andUserIdEqualTo(uId);
            criteria.andRoleIdEqualTo(rid);
            List<UserRolePO> resultList=userRolePOMapper.selectByExample(example);
            if(resultList.isEmpty()){
                returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "没有这个条目，请检查用户id和角色id是否正确");
            }
            else{
                Long id=resultList.get(0).getId();
                int a=userRolePOMapper.deleteByPrimaryKey(id);
                String userKey="u_"+uId;
                if(redisTemplate.hasKey(userKey)) redisTemplate.delete(userKey);
                loadAllPrivilege(uId);
                returnObject=new ReturnObject(a);
            }
        }catch (Exception e) {
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
//          e.printStackTrace();
        }
        return returnObject;
    }
    /**
     * 查询某个id用户所拥有的角色信息
     * @param tid 用户的id
     * @return 该用户角色信息
     * Create By GXC
     */
    public ReturnObject selectUserRoleByuId(Long tid){
        ReturnObject returnObject = null;
        try {
            UserRolePOExample example=new UserRolePOExample();
            UserRolePOExample.Criteria criteria=example.createCriteria();
            if(tid!=null) criteria.andUserIdEqualTo(tid);
            List<UserRolePO> resultList=userRolePOMapper.selectByExample(example);
            List<UserRoleRetVO> returnList=new ArrayList<UserRoleRetVO>();
            for(UserRolePO userRolePO:resultList){
                UserSimpleRetVo userSimpleRetVo=new UserSimpleRetVo(userPOMapper.selectByPrimaryKey(userRolePO.getUserId()));
                RoleSimpleRetVo roleSimpleRetVo=new RoleSimpleRetVo(rolePOMapper.selectByPrimaryKey(userRolePO.getRoleId()));
                UserSimpleRetVo creatorUser=new UserSimpleRetVo(userPOMapper.selectByPrimaryKey(userRolePO.getCreatorId()));
                UserRoleRetVO userRoleRetVO=new UserRoleRetVO(userRolePO,userSimpleRetVo,roleSimpleRetVo,creatorUser);
                returnList.add(userRoleRetVO);
            }
            returnObject=new ReturnObject<List<UserRoleRetVO>>(returnList);
        }catch (Exception e) {
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
//          e.printStackTrace();
        }
        return returnObject;
    }

    /**
     * 将登陆用户的权限都放到redis中
     *
     * @param userId
     * @author GXC
     */
    public void loadAllPrivilege(Long userId) {
        UserRolePOExample rolePOExample = new UserRolePOExample();
        UserRolePOExample.Criteria criteria = rolePOExample.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<UserRolePO> userRolePOS = userRolePOMapper.selectByExample(rolePOExample);
        List<Long> allRoleId = new ArrayList<>();
        for(UserRolePO po:userRolePOS){
            allRoleId.add(po.getRoleId());
        }
        String key = "u_" + userId;
        Set<String> roleKeys = new HashSet<>(allRoleId.size());
        for (Long roleId : allRoleId) {
            String roleKey = "r_" + roleId;
            roleKeys.add(roleKey);
            if (!redisTemplate.hasKey(roleKey)) {
                roleDao.loadRolePriv(roleId);
            }
            //取第一个集合的set放到第二个set
            redisTemplate.opsForSet().unionAndStore(roleKeys, key);
        }
        long randTimeout = Common.addRandomTime(timeout);
        redisTemplate.expire(key, randTimeout, TimeUnit.SECONDS);
    }

    /**
     * 用户登出
     *
     * @param userId 用户id
     * @return
     * @author GXC
     */
    public ReturnObject userLogout(Long userId) {
        ReturnObject returnObject = null;
        String key = "u_login_" + userId;
        if (redisTemplate.hasKey(key)) {
            logger.debug("logout:userId:"+userId);
            redisTemplate.delete(key);
        }
        returnObject = new ReturnObject();
        return returnObject;
    }
}