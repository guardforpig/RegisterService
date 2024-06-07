package com.middleware.server.annotation;


import com.middleware.server.common.ResponseCode;
import com.middleware.server.common.ResponseUtil;
import com.middleware.server.common.TokenJwt;
import com.middleware.server.util.RedisUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author Tong
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    @Autowired
    RedisUtil redisUtil;
    //Controller层切点
    @Pointcut("@annotation(com.middleware.server.annotation.Audit)")
    public void auditAspect() {
    }

    /**
     * 前置通知 用于拦截Controller层记录用户的操作
     *
     * @param joinPoint 切点
     */
    @Before("auditAspect()")
    public void doBefore(JoinPoint joinPoint) {
    }

    //配置controller环绕通知,使用在方法aspect()上注册的切入点
    @Around("auditAspect()")
    public Object around(JoinPoint joinPoint) {
        System.out.println(111);
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        String token = request.getHeader(TokenJwt.LOGIN_TOKEN_KEY);
        if (token == null) {
            logger.info("token为空");
            System.out.println("空");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return ResponseUtil.fail(ResponseCode.AUTH_NEED_LOGIN);
        }

        // 校验秘钥
        Long userId = null;
        try {
            TokenJwt.TokenClaimMsg tokenClaimMsg = new TokenJwt().verifyTokenAndGetClaims(token);
            userId = tokenClaimMsg.getUserId();
            if (userId == null) {
                logger.info("校验失败，用户id没有");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                System.out.println("id不存在");
                return ResponseUtil.fail(ResponseCode.AUTH_NEED_LOGIN);
            }else{
                String redisToken=(String)redisUtil.get("u_login_"+userId);
                if(redisToken!=null&&!redisToken.equals(token)){
                    logger.info("该账号已被他人登录，如非本人授权，请尽快修改密码");
                    System.out.println("已被登录");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return ResponseUtil.fail(ResponseCode.AUTH_NEED_LOGIN);
                }
            }
            if (null == tokenClaimMsg) {
                System.out.println(token);
                logger.info("校验失败，载荷不对");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return ResponseUtil.fail(ResponseCode.AUTH_NOT_ALLOW);
            }
        } catch (Exception e) {
            logger.info("token校验失败");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("校验失败");
            return ResponseUtil.fail(ResponseCode.AUTH_NEED_LOGIN);
        }

//        //检验api中传入token是否和userId一致
//        String pathInfo=request.getPathInfo();
//        if(null!=pathInfo)
//        {
//
//            String[] paths =pathInfo.split("/");
//            for(int i=0;i<paths.length;i++){
//
//                if("userId".equals(paths[i])){
//                    if(i+1<paths.length){
//                        //找到路径上对应userId 将其与mobile比较
//                        String pathId=paths[i+1];
//
//                        if(!pathId.equals(userId.toString())){
//                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                            return ResponseUtil.fail(ResponseCode.FIELD_NOTVALID, "手机不匹配");
//                        }
////                        logger.debug("success match Id!");
//                    }
//                    break;
//                }
//            }
//        }


        Object[] args = joinPoint.getArgs();
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Object param = args[i];
            Annotation[] paramAnn = annotations[i];
            if (paramAnn.length == 0) {
                continue;
            }

            for (Annotation annotation : paramAnn) {
                //这里判断当前注解是否为LoginUser.class
                if (annotation.annotationType().equals(LoginUser.class)) {
                    //校验该参数，验证一次退出该注解
                    args[i] = userId;
                }
//                if (annotation.annotationType().equals(Depart.class)) {
//                    //校验该参数，验证一次退出该注解
//                    args[i] = departId;
//                }
            }
        }

        Object obj = null;
        try {
            obj = ((ProceedingJoinPoint) joinPoint).proceed(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return obj;
    }
}
