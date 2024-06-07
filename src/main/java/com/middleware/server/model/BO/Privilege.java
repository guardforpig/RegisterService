package com.middleware.server.model.BO;

import com.middleware.server.common.Common;
import com.middleware.server.model.PO.PrivilegePO;
import com.middleware.server.model.VO.PrivilegeSimpleRetVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Privilege {
    /**
     * 请求类型
     */
    public enum RequestType {
        GET(0, "GET"),
        POST(1, "POST"),
        PUT(2, "PUT"),
        DELETE(3, "DELETE");

        private static final Map<Integer, RequestType> typeMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            typeMap = new HashMap();
            for (RequestType enum1 : values()) {
                typeMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        RequestType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static RequestType getTypeByCode(Integer code) {
            return typeMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

    }

    private Long id;

    private String name;

    private String url;

    private RequestType requestType;

    private String signature;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    /**
     * privilege的key
     */
    private String key;

    /**
     * 计算出的签名
     */
    private String cacuSignature;

    /**
     * redis里作为hashKey的值
     */
    private String hKey;
    /**
     * 构造函数
     *
     * @param po 用PO构造
     */
    public Privilege(PrivilegePO po) {
        this.id = po.getId();
        this.name = po.getName();
        this.url = po.getUrl();
        this.signature = po.getSignature();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
        this.requestType = RequestType.getTypeByCode(po.getRequestType().intValue());

        StringBuilder signature1 = Common.concatString("-", po.getRequestType().toString(), po.getUrl());
        this.key = signature1.toString();
        this.hKey = "p_"+ po.getId();
        signature1.append("-");
        signature1.append(po.getId());
        this.cacuSignature = Common.getSHA256(signature1.toString());
    }

    /**
     * 对象未篡改
     * @return
     */
    public Boolean authetic() {
        return this.cacuSignature.equals(this.signature);
    }



    public PrivilegeSimpleRetVO createSimpleVO() {
        return new PrivilegeSimpleRetVO(this.id,this.url,this.requestType.getCode());
    }
}
