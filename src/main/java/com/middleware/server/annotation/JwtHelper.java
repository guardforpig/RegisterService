package com.middleware.server.annotation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.middleware.server.common.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtHelper {
    private Logger logger = LoggerFactory.getLogger(JwtHelper.class);
    static final String SECRET = "Role-Privilege-Token";
    static final String ISSUSER = "OOAD";
    static final String SUBJECT = "this is a token";
    static final String AUDIENCE = "MINIAPP";
    public static final String LOGIN_TOKEN_KEY = "authorization";

    public JwtHelper() {
    }

    public String createToken(Long userId, String userName, Long departId, Integer userLevel, int expireTime) {
        this.logger.debug("createToken:");

        try {
            Algorithm algorithm = Algorithm.HMAC256("Role-Privilege-Token");
            Map<String, Object> map = new HashMap();
            Date nowDate = new Date();
            Date expireDate = this.getAfterDate(nowDate, 0, 0, 0, 0, 0, expireTime);
            map.put("alg", "HS256");
            map.put("typ", "JWT");
            String tokenId = Common.genSeqNum();
            StringBuilder message = (new StringBuilder()).append("createToken: ").append("userId = ").append(userId).append(" userName=").append(userName).append(" departId=").append(departId).append(" tokenId:").append(tokenId);
            this.logger.debug(message.toString());
            String token = JWT.create().withHeader(map).withClaim("userId", userId).withClaim("departId", departId).withClaim("tokenId", tokenId).withClaim("userName", userName).withClaim("userLevel", userLevel).withIssuer("OOAD").withSubject("this is a token").withAudience(new String[]{"MINIAPP"}).withIssuedAt(nowDate).withExpiresAt(expireDate).sign(algorithm);
            return token;
        } catch (JWTCreationException var13) {
            var13.printStackTrace();
            return null;
        }
    }

    public UserAndDepart verifyTokenAndGetClaims(String token) {
        if (token != null && !token.isEmpty()) {
            try {
                Algorithm algorithm = Algorithm.HMAC256("Role-Privilege-Token");
                JWTVerifier verifier = JWT.require(algorithm).withIssuer("OOAD").build();
                DecodedJWT jwt = verifier.verify(token);
                Map<String, Claim> claims = jwt.getClaims();
                Claim claimUserId = (Claim)claims.get("userId");
                Claim claimDepartId = (Claim)claims.get("departId");
                Claim claimUserName = (Claim)claims.get("userName");
                Claim claimUserLevel = (Claim)claims.get("userLevel");
                Claim expireTime = (Claim)claims.get("exp");
                return claimDepartId == null ? new UserAndDepart(claimUserId.asLong(), claimUserName.asString(), 0L, claimUserLevel.asInt(), expireTime.asDate()) : new UserAndDepart(claimUserId.asLong(), claimUserName.asString(), claimDepartId.asLong(), claimUserLevel.asInt(), expireTime.asDate());
            } catch (JWTVerificationException var11) {
                return null;
            }
        } else {
            return null;
        }
    }

    public Date getAfterDate(Date date, int year, int month, int day, int hour, int minute, int second) {
        if (date == null) {
            date = new Date();
        }

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        if (year != 0) {
            cal.add(1, year);
        }

        if (month != 0) {
            cal.add(2, month);
        }

        if (day != 0) {
            cal.add(5, day);
        }

        if (hour != 0) {
            cal.add(11, hour);
        }

        if (minute != 0) {
            cal.add(12, minute);
        }

        if (second != 0) {
            cal.add(13, second);
        }

        return cal.getTime();
    }

    public class UserAndDepart {
        private Long userId;
        private String userName;
        private Long departId;
        private Date expireTime;
        private Integer userLevel;

        public UserAndDepart(long userId, String userName, long departId, Integer userLevel, Date expireTime) {
            this.userId = userId;
            this.userName = userName;
            this.departId = departId;
            this.expireTime = expireTime;
            this.userLevel = userLevel;
        }

        public Long getUserId() {
            return this.userId;
        }

        public Long getDepartId() {
            return this.departId;
        }

        public String getUserName() {
            return this.userName;
        }

        public Date getExpTime() {
            return this.expireTime;
        }

        public Integer getUserLevel() {
            return this.userLevel;
        }
    }
}