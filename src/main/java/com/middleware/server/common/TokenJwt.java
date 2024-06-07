package com.middleware.server.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.*;

/**
 * @author GXC
 */
public class TokenJwt {

    /**
     * 秘钥
     */
    static final String SECRET = "Small-term";
    /**
     * 签名是由谁生成
     */
    static final String ISSUSER = "ONE";
     /**
      * 签名的主题
      */
    static final String SUBJECT = "this is a token";
    /**
     * 签名的观众
     */
    static final String AUDIENCE = "MINIAPP";
    /**
     * Request中的变量名
     */
    public static final String LOGIN_TOKEN_KEY = "authorization";

    /**
     * 获取token里的信息用于校验
     */
    public class TokenClaimMsg{
        private Long userId;

        private Date exprieTime;

        public TokenClaimMsg(long userId, Long exprieTime){
            this.userId = userId;
            this.exprieTime = new Date(exprieTime * 1000);
        }

        public Long getUserId() {
            return userId;
        }

        public Date getExprieTime() {
            return exprieTime;
        }
    }

    /**
     * 创建用户Token
     * @param userId 用户id
     * @return token
     */
    public String creatToken(Long userId,int expireTime){
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            Map<String,Object> map = new HashMap<>();
            Date nowDate = new Date();
            Date expireDate = getAfterDate(nowDate, 0, 0, 0, 0, 0, expireTime);
            map.put("alg", "HS256");
            map.put("typ", "JWT");
            String tokenId = Common.genSeqNum();
            String token = JWT.create()
                    // 设置头部信息 Header
                    .withHeader(map)
                    // 设置 载荷 Payload
                    .withClaim("userId", userId)
                    .withClaim("tokenId",tokenId)
                    .withIssuer(ISSUSER)
                    .withSubject(SUBJECT)
                    .withAudience(AUDIENCE)
                    // 生成签名的时间
                    .withIssuedAt(nowDate)
                    // 签名过期的时间
                    .withExpiresAt(expireDate)
                    // 签名 Signature
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException e) {
            return null;
        }

    }

    public TokenClaimMsg verifyTokenAndGetClaims(String token){
        if(token == null ||token.isEmpty()){
            System.out.println(token);
            return null;
        }
        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUSER)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Claim> claims = jwt.getClaims();
            Claim claimUserId = claims.get("userId");
            Claim claimExprieTime = claims.get("exp");
            return new TokenClaimMsg(claimUserId.asLong(), claimExprieTime.asLong());
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    /**
     * 获取过期时间
     * @param date 当前日期
     * @param year 年
     * @param month 月
     * @param day 日
     * @param hour 时
     * @param minute 分
     * @param second 秒
     * @return 过期时间
     */
    public Date getAfterDate(Date date, int year, int month, int day, int hour, int minute, int second) {
        if (date == null) {
            date = new Date();
        }

        Calendar cal = new GregorianCalendar();

        cal.setTime(date);
        if (year != 0) {
            cal.add(Calendar.YEAR, year);
        }
        if (month != 0) {
            cal.add(Calendar.MONTH, month);
        }
        if (day != 0) {
            cal.add(Calendar.DATE, day);
        }
        if (hour != 0) {
            cal.add(Calendar.HOUR_OF_DAY, hour);
        }
        if (minute != 0) {
            cal.add(Calendar.MINUTE, minute);
        }
        if (second != 0) {
            cal.add(Calendar.SECOND, second);
        }
        return cal.getTime();
    }

//    public static void main(String[] args) {
//        TokenJwt tokenJwt = new TokenJwt();
//        String token = tokenJwt.creatToken(123L,"111518",10);
//        System.out.println(token);
//        System.out.println(tokenJwt.verifyTokenAndGetClaims(token).getUserId());
//        System.out.println(tokenJwt.verifyTokenAndGetClaims(token).getUsername());
//    }
}
