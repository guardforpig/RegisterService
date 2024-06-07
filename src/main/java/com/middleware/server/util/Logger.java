package com.middleware.server.util;


import com.middleware.server.mapper.LogPOMapper;
import com.middleware.server.model.PO.LogPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * @author ASUS
 */
@Repository
public class Logger {
    @Autowired
    LogPOMapper logPOMapper;

    public Boolean writeLog(Log log, String method,String operation){
        LogPO logPO = new LogPO();
        logPO.setBelongClass(log.getBelongClass());
        logPO.setGmtCreate(LocalDateTime.now());
//        logPO.setBelongModule(log.getModule());
        logPO.setOperation(operation);
        logPO.setGmtModified(LocalDateTime.now());
        logPO.setMethod(method);
        try{
            System.out.println(logPOMapper);
            logPOMapper.insert(logPO);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
