package com.middleware.server.console;

import com.middleware.server.common.ResponseCode;
import com.middleware.server.common.ReturnObject;
import com.middleware.server.mapper.PrivilegePOMapper;
import com.middleware.server.mapper.RolePrivilegePOMapper;
import com.middleware.server.mapper.UserRolePOMapper;
import com.middleware.server.model.BO.Privilege;
import com.middleware.server.model.BO.PrivilegeBO;
import com.middleware.server.model.PO.*;
import com.middleware.server.model.VO.PrivilegeRouteVO;
import com.middleware.server.model.VO.PrivilegeVO;
import com.middleware.server.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class PrivilegeDao implements InitializingBean {


    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    PrivilegePOMapper privilegePOMapper;

    @Autowired
    UserRolePOMapper userRolePOMapper;

    @Autowired
    RolePrivilegePOMapper rolePrivilegePOMapper;

    @Autowired
    com.middleware.server.util.Logger logger1;
    private static final Logger logger = LoggerFactory.getLogger(PrivilegeDao.class);
    public final Log log=new Log(this.getClass().getName(),"log");


    /**
     * 将权限加到redis中
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(2222222);
        PrivilegePOExample example = new PrivilegePOExample();
        PrivilegePOExample.Criteria criteria = example.createCriteria();
        criteria.andRequestTypeLessThan(8);
        List<PrivilegePO> privilegePOS = privilegePOMapper.selectByExample(example);
        for (PrivilegePO privilegePO : privilegePOS) {
            Privilege privilege = new Privilege(privilegePO);
            //检验权限是否被篡改,初始化的情况下为空
            if (privilege.authetic()) {
                boolean a = redisTemplate.opsForHash().putIfAbsent("Priv", privilege.getHKey(), privilege.getKey());
                logger.debug("权限没有篡改，添加到redis,Hkey=" + privilege.getHKey() + ",value=" + privilege.getKey());
            } else {
                System.out.println(privilege.getSignature());
                logger.debug(privilege.getSignature());
                if (privilege.getSignature() == null) {
                    boolean a = redisTemplate.opsForHash().putIfAbsent("Priv", privilege.getHKey(), privilege.getKey());
                    logger.debug("权限未初始化，添加到redis,Hkey=" + privilege.getHKey() + ",value=" + privilege.getKey());
                } else {
                    logger.debug("权限被篡改");
                }
            }
        }

    }

    /**
     * @author GXC
     * 给所有权限加签名
     */
    public void initialize() {
        Set<String> keys = redisTemplate.keys("*");
        System.out.println(keys);
        PrivilegePOExample example = new PrivilegePOExample();
        List<PrivilegePO> privilegePOS = privilegePOMapper.selectByExample(example);
        for (PrivilegePO privilegePO : privilegePOS) {
            Privilege privilege = new Privilege(privilegePO);
            PrivilegePO po = new PrivilegePO();
            po.setId(privilegePO.getId());
            logger.debug("privilege" + po.getId() + "initialize signature");
            po.setSignature(privilege.getCacuSignature());
            privilegePOMapper.updateByPrimaryKeySelective(po);
        }
    }

//-----------------------------权限部分查找与替换----------------------------------------------------------------
//
//
//    public void addChild(ArrayList<Long> ids, ArrayList<Boolean> isSelect, ArrayList<PrivilegeRouteVO> vos, List<PrivilegePO> privilegePOS, PrivilegePO father, PrivilegePO child) {
//
//        if (!isSelect.get(ids.indexOf(father.getId()))) {
//            PrivilegePO dchild = father;
//            PrivilegePO dfather = null;
//            for (PrivilegePO po : privilegePOS) {
//                PrivilegeBO bo = new PrivilegeBO(dchild);
//                if (po.getId().equals(bo.getParent().get(0))) {
//                    dfather = po;
//                    break;
//                }
//            }
//            addChild(ids, isSelect, vos, privilegePOS, dfather, dchild);
//        } else {
//            int fatherIndex = privilegePOS.indexOf(father);
//            int childIndex = privilegePOS.indexOf(child);
//            //如果request_type为10的话，为9就action里
//            if (child.getRequestType() == 10) {
//                vos.get(fatherIndex).getActions().add(child.getUrl());
//            }
//            //其他情况为9
//            else {
//                vos.get(fatherIndex).getChildren().add(vos.get(childIndex));
//            }
//            isSelect.set(childIndex, true);
//        }
//    }
//
//    public Long getParentId(Long privilegeId) {
//        PrivilegePO po = privilegePOMapper.selectByPrimaryKey(privilegeId);
//        PrivilegeBO bo = new PrivilegeBO(po);
//        return bo.getParent().get(0);
//    }

//    /**
//     * 获取权限的子权限的权限字符串
//     *
//     * @author GXC
//     * @param privilegeId 权限id
//     * @return 权限字符串
//     */
//    public List<String> getChild(Long privilegeId) {
//        PrivilegePOExample example = new PrivilegePOExample();
//        PrivilegePOExample.Criteria criteria = example.createCriteria();
//        criteria.andRequestTypeLessThan(8);
//        List<String> rets = new ArrayList<>();
//        try {
//            List<PrivilegePO> pos = privilegePOMapper.selectByExample(example);
//            logger.debug("getChild,获取权限id为" + privilegeId + "的子权限");
//            for (PrivilegePO po : pos) {
//                PrivilegeBO bo = new PrivilegeBO(po);
//                Privilege privilege = new Privilege(po);
//                String s = (String) redisTemplate.opsForHash().get("Priv", privilege.getHKey());
//                logger.debug("getChild获取redis中hkey为："+privilege.getHKey()+"的值");
//                if (bo.getParent().contains(privilegeId)) {
//                    if (!rets.contains(s)) {
//                        rets.add(s);
//                    }
//                }
//            }
//        } catch (Exception e){
//            logger.debug("getChild服务器错误");
//        }
//
//        return rets;
//    }
}
