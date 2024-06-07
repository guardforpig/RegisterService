package com.middleware.server.annotation;


import com.middleware.server.common.ResponseCode;
import com.middleware.server.common.ResponseUtil;
import com.middleware.server.common.TokenJwt;
import com.middleware.server.console.UserDao;
import com.middleware.server.model.BO.Privilege;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author GXC
 */
@Order(2)
@Aspect
@Component
public class ActionAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    private UserDao userDao;

    private Pattern requestPattern = Pattern.compile("/([0-9])+");
    private Pattern privilegePattern = Pattern.compile("/\\{[a-z]*}");

    //Controller层切点
    @Pointcut("@annotation(com.middleware.server.annotation.Authorization)")
    public void ActionInterceptor() {
    }

    /**
     * 前置通知 用于拦截Controller层记录用户的操作
     *
     * @param joinPoint 切点
     */
    @Before("ActionInterceptor()")
    public void doBefore(JoinPoint joinPoint) {
    }

    //配置controller环绕通知,使用在方法aspect()上注册的切入点
    @Around("ActionInterceptor()")
    public Object around(JoinPoint joinPoint) {
        System.out.println(222);
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //查看token过期时间，如果过期则焕发新的token
        String token = request.getHeader(TokenJwt.LOGIN_TOKEN_KEY);
        TokenJwt.TokenClaimMsg tokenClaimMsg = new TokenJwt().verifyTokenAndGetClaims(token);
        Date expireTime = tokenClaimMsg.getExprieTime();
        Long userId = tokenClaimMsg.getUserId();
        Long sec = expireTime.getTime() - System.currentTimeMillis();
        String userKey = "u_login_" + userId;
        if (sec < 30 || !redisTemplate.hasKey(userKey)) {
            TokenJwt tokenJwt = new TokenJwt();
            Long exTime = 60*60*5L;
            logger.info("创造新的token");
            token = tokenJwt.creatToken(userId, Math.toIntExact(exTime));
            //将token的时间延长,token焕发
            if (redisTemplate.hasKey(userKey)) {
                redisTemplate.opsForValue().set(userKey, token, exTime, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(userKey, token, exTime, TimeUnit.SECONDS);
                redisTemplate.opsForValue().set(userKey, token, exTime, TimeUnit.SECONDS);
            }
            response.addHeader("token",token);
        }
        //取出请求的关键信息-正则
        Privilege.RequestType type = Privilege.RequestType.valueOf(request.getMethod().toUpperCase());
        //对url处理
        String url = request.getRequestURL().toString();
        String s1 = url.substring(url.indexOf("://") + 3);
        System.out.println(s1);
        s1 = s1.substring(s1.indexOf("/"));
        String requestValue = type.getCode() + "-" + s1;
        Matcher matcherRequest = requestPattern.matcher(requestValue);
        String requestCommon = matcherRequest.replaceAll("/{id}");
        System.out.println(requestCommon);
        String userPrivKey = "u_" + userId;
        Boolean exitFlag = false;
        //用户的权限是否在redis里
        //不在就将权限放到redis中
        if (!redisTemplate.hasKey(userPrivKey)) {
            logger.info("获取全部的权限");
            userDao.loadAllPrivilege(userId);
        }
        Set privilegeSet = redisTemplate.opsForSet().members(userPrivKey);
        for (Object value : privilegeSet) {
            Matcher matcherPrivilege = privilegePattern.matcher((String) value);
            String privilegeCommon = matcherPrivilege.replaceAll("/{id}");
            logger.info("userId:"+userId+",privilege:common string:"+privilegeCommon);
            if (privilegeCommon.equals(requestCommon)) {
                exitFlag = true;
            }
        }

        if (!exitFlag) {
            logger.info("用户没有权限访问"+request.getRequestURL());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return ResponseUtil.fail(ResponseCode.AUTH_NOT_ALLOW);
        }
        System.out.println(requestCommon);

        Object[] args = joinPoint.getArgs();
        Object obj = null;
        try {
            obj = ((ProceedingJoinPoint) joinPoint).proceed(args);
        } catch (Throwable e) {

        }
        return obj;
    }


}
