package com.middleware.server.common;

import com.github.pagehelper.PageInfo;
import com.middleware.server.model.VOObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author copy
 */
public class Common {


    /**
     * 生成八位数序号
     * 目的：因为token里面的过期时间是以秒记的，同一秒生成的token会是相同的
     * @return 序号
     */
    public static String genSeqNum(){
        int  maxNum = 36;
        int i;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmssS");
        LocalDateTime localDateTime = LocalDateTime.now();
        String strDate = localDateTime.format(dtf);
        StringBuffer sb = new StringBuffer(strDate);

        int count = 0;
        char[] str = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        Random r = new Random();
        while(count < 2){
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < str.length) {
                sb.append(str[i]);
                count ++;
            }
        }
        return sb.toString();
    }

    /**
     * 处理返回对象
     * @param returnObject 返回的对象
     * @return
     */
    public static Object getListRetObject(ReturnObject<List> returnObject) {
        ResponseCode code = returnObject.getCode();
        switch (code){
            case OK:
                List objs = returnObject.getData();
                if (objs != null){
                    List<Object> ret = new ArrayList<>(objs.size());
                    for (Object data : objs) {
                        if (data instanceof VOObject) {
                            ret.add(((VOObject)data).createVo());
                        }
                    }
                    return ResponseUtil.ok(ret);
                }else{
                    return ResponseUtil.ok();
                }
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

    /**
     * 处理分页返回对象
     * @param returnObject 返回的对象
     * @return
     */
    public static Object getPageRetObject(ReturnObject<PageInfo<VOObject>> returnObject) {
        ResponseCode code = returnObject.getCode();
        switch (code){
            case OK:

                PageInfo<VOObject> objs = returnObject.getData();
                if (objs != null){
                    List<Object> voObjs = new ArrayList<>(objs.getList().size());
                    for (Object data : objs.getList()) {
                        if (data instanceof VOObject) {
                            voObjs.add(((VOObject)data).createVo());
                        }
                    }

                    Map<String, Object> ret = new HashMap<>();
                    ret.put("list", voObjs);
                    ret.put("total", objs.getTotal());
                    ret.put("page", objs.getPageNum());
                    ret.put("pageSize", objs.getPageSize());
                    ret.put("pages", objs.getPages());
                    return ResponseUtil.ok(ret);
                }else{
                    return ResponseUtil.ok();
                }
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }
    /**
     * 范式版处理分页返回对象
     * @param returnObject 返回的对象
     * @return
     */
    public static<T> Object getPageGenericRetObject(ReturnObject<PageInfo<T>> returnObject) {
        ResponseCode code = returnObject.getCode();
        switch (code){
            case OK:

                PageInfo<T> objs = returnObject.getData();
                if (objs != null){
                    List<Object> voObjs = new ArrayList<>(objs.getList().size());
                    for (Object data : objs.getList()) {
                        if (data instanceof VOObject) {
                            voObjs.add(((VOObject)data).createVo());
                        }
                    }

                    Map<String, Object> ret = new HashMap<>();
                    ret.put("list", voObjs);
                    ret.put("total", objs.getTotal());
                    ret.put("page", objs.getPageNum());
                    ret.put("pageSize", objs.getPageSize());
                    ret.put("pages", objs.getPages());
                    return ResponseUtil.ok(ret);
                }else{
                    return ResponseUtil.ok();
                }
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }
    /**
     * 根据 errCode 修饰 API 返回对象的 HTTP Status
     * @param returnObject 原返回 Object
     * @return 修饰后的返回 Object
     */
    public static Object decorateReturnObject(ReturnObject returnObject) {
        switch (returnObject.getCode()) {
            case RESOURCE_ID_NOTEXIST:
                // 404：资源不存在
                return new ResponseEntity(
                        ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg()),
                        HttpStatus.NOT_FOUND);
            case INTERNAL_SERVER_ERR:
                // 500：数据库或其他严重错误
                return new ResponseEntity(
                        ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg()),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            case OK:
                // 200: 无错误
                Object data = returnObject.getData();
                if (data != null){
                    return ResponseUtil.ok(data);
                }else{
                    return ResponseUtil.ok();
                }
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

    /**
     * 动态拼接字符串，主要用于生成签名
     * @param sep 分隔符
     * @param fields 拼接的字符串
     * @return StringBuilder
     * @author tong zhenyu
     * time 2021年7月7日17:36:11
     */
    public static StringBuilder concatString(String sep, String... fields){
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i< fields.length; i++){
            if (i > 0){
                ret.append(sep);
            }
            ret.append(fields[i]);
        }
        return ret;
    }

    /**
     * 利用java原生的类实现SHA256加密
     *
     * @param str 加密后的报文
     * @return
     */
    public static String getSHA256(String str) {
        if (str == null){
            return null;
        }

        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    /**
     * 增加20%以内的随机时间
     * 如果timeout <0 则会返回60s+随机时间
     * @param timeout 时间
     * @return 增加后的随机时间
     */
    public static long addRandomTime(long timeout) {
        if (timeout <= 0) {
            timeout = 60;
        }
        //增加随机数，防止雪崩
        timeout += (long) new Random().nextDouble() * (timeout / 5 - 1);
        return timeout;
    }
}
