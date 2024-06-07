package com.middleware.server;

import com.middleware.server.console.PrivilegeDao;
import com.middleware.server.console.RoleDao;
import com.middleware.server.console.UserDao;
import com.middleware.server.naming.GroupInfo;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.middleware.server.Service.RegisterService.groupMap;
@SpringBootApplication(scanBasePackages = {"com.middleware.server"})
@EnableConfigurationProperties
@MapperScan("com.middleware.server.mapper")
@Slf4j
public class ServerApplication implements ApplicationRunner {
    @Autowired
    private PrivilegeDao privilegeDao;

    @Autowired
    private UserDao userDao;
    private static final Logger logger = LoggerFactory.getLogger(PrivilegeDao.class);
    @Autowired
    private RoleDao roleDao;
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        executeFixedDelay();
    }
    /*
    定期刷新实例状态
     */
    public static void executeFixedDelay() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(
                ()->{
                    System.out.println("flush");
                    if(!groupMap.isEmpty()){
                        for(String key:groupMap.keySet()){
                            GroupInfo groupInfo=groupMap.get(key);
                            try {
                                groupInfo.flush();
                                if(groupInfo.getServiceMap().isEmpty()) groupMap.remove(key);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                0,
                15,
                TimeUnit.SECONDS);
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.debug("Initialize......");
        privilegeDao.initialize();
        roleDao.initialize();
        userDao.initialize();
    }
}
