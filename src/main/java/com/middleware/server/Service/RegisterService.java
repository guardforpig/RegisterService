package com.middleware.server.Service;

import com.alibaba.fastjson.JSON;
import com.middleware.server.POJO.Member;
import com.middleware.server.naming.GroupInfo;
import com.middleware.server.naming.ServiceInfo;
import com.middleware.server.util.BroadcastUdp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
@RequestMapping()
public class RegisterService {
    public static ConcurrentHashMap<String, GroupInfo> groupMap=new ConcurrentHashMap<String,GroupInfo>();
    //200，注册成功或者已经注册,udp告知订阅该服务的人
    @PostMapping("/register")
    public Object register(@RequestParam("ip")String ip,
                            @RequestParam("port")String port,
                            @RequestParam("ServiceName")String serviceName,
                            @RequestParam("GroupName")String groupName,
                            @RequestParam(value = "providers",required = false) String providers) throws InterruptedException {
        Map<String,Object> obj=new HashMap<>();
        Thread.sleep(5);
        try{
            List<String> providerList= Arrays.asList(providers.split(","));
            Set<String> providerSet=new HashSet<>();
            providerSet.addAll(providerList);
            GroupInfo group=getGroup(groupName);
            Member member=new Member(ip,port);
            member.setProviders(providerSet);
            ServiceInfo service;
            if(group.checkService(serviceName)){
                service=group.getService(serviceName);
                service.newMember(member);
            }else{
                service=group.newService(serviceName);
                service.newMember(member);
            }
            if(providers==null||providers.isBlank()){
                obj.put("producers",null);
            }else{
                obj.put("producers",group.subscribe(providerSet,member));
            }
            Map<String,ServiceInfo> map=new HashMap<>();
            map.put(serviceName,service);
            new BroadcastUdp(JSON.toJSONString(map),group.getConsumers(serviceName)).start();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            obj.put("errno",500);
            return new ResponseEntity(obj, HttpStatus.OK);
        }
        obj.put("errno",200);
        System.out.println("register:"+ip+":"+port+":"+serviceName+":"+groupName);
        return new ResponseEntity(obj, HttpStatus.OK);
    }
    /*
    检测是否存在，不存在返回203，存在刷新alive状态返回200
     */
    @PostMapping("/beat")
    public Object beat(@RequestParam("ip")String ip,
                           @RequestParam("port")String port,
                           @RequestParam("ServiceName")String serviceName,
                           @RequestParam("GroupName")String groupName){
        System.out.println("beat:"+ip+":"+port+":"+serviceName+":"+groupName);
        Map<String,Object> obj=new HashMap<>();
        try{
            GroupInfo groupInfo=groupMap.get(groupName);
            if(groupInfo==null){
                obj.put("errno",203);
                obj.put("errmsg","组不存在");
                return new ResponseEntity(obj, HttpStatus.OK);
            }
            ServiceInfo serviceInfo=groupInfo.getService(serviceName);
            if(serviceInfo==null){
                obj.put("errno",203);
                obj.put("errmsg","服务不存在");
                return new ResponseEntity(obj, HttpStatus.OK);
            }
            Member member=new Member(ip,port);
            if(!serviceInfo.checkMember(member)){
                obj.put("errno",203);
                obj.put("errmsg","实例未注册");
                return new ResponseEntity(obj, HttpStatus.OK);
            }else{
                serviceInfo.reAlive(member);
                obj.put("errno",200);
                return new ResponseEntity(obj, HttpStatus.OK);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            obj.put("errno",500);
            return new ResponseEntity(obj, HttpStatus.OK);
        }
    }

    /*
      获取服务提供者
     */
    @PostMapping("/loadProviders")
    public Object loadProviders(@RequestParam("ip")String ip,
                       @RequestParam("port")String port,
                       @RequestParam("ServiceName")String serviceName,
                       @RequestParam("GroupName")String groupName,
                       @RequestParam(value = "providers",required = false) String providers){
        Map<String,Object> obj=new HashMap<>();
        try{
            List<String> providerList= Arrays.asList(providers.split(","));
            Set<String> providerSet=new HashSet<>();
            providerSet.addAll(providerList);
            GroupInfo group=getGroup(groupName);
            Member member=new Member(ip,port);
            member.setProviders(providerSet);
            ServiceInfo service=groupMap.get(groupName).getService(serviceName);
            if(service==null){
                obj.put("errno",200);
                return new ResponseEntity(obj, HttpStatus.OK);
            }
            service.newMember(member);
            obj.put("producers",group.subscribe(providerSet,member));
            obj.put("errno",200);
            return new ResponseEntity(obj, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            obj.put("errno",500);
            return new ResponseEntity(obj, HttpStatus.OK);
        }
    }

    @PostMapping("/logout")
    public Object logout(@RequestParam("ip")String ip,
                                @RequestParam("port")String port,
                                @RequestParam("ServiceName")String serviceName,
                                @RequestParam("GroupName")String groupName,
                                @RequestParam(value = "providers",required = false) String providers){
        Map<String,Object> obj=new HashMap<>();
        try{
            List<String> providerList= Arrays.asList(providers.split(","));
            Set<String> providerSet=new HashSet<>();
            providerSet.addAll(providerList);
            GroupInfo group=getGroup(groupName);
            Member member=new Member(ip,port);
            member.setProviders(providerSet);
            ServiceInfo service=group.getService(serviceName);
            if(service!=null)service.deleteMember(member);
            group.relieveSub(member);
            Map<String,ServiceInfo> map=new HashMap<>();
            map.put(serviceName,service);
            List<Member> subscribers=group.getConsumers(serviceName);
            if(subscribers!=null){
                new BroadcastUdp(JSON.toJSONString(map),subscribers).start();
            }
            obj.put("errno",200);
            return new ResponseEntity(obj, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            obj.put("errno",500);
            return new ResponseEntity(obj, HttpStatus.OK);
        }
    }

    GroupInfo getGroup(String groupName){
        if(checkGroup(groupName)){
            return groupMap.get(groupName);
        }else{
            GroupInfo groupInfo=new GroupInfo();
            groupMap.put(groupName,groupInfo);
            return groupInfo;
        }
    }

    boolean checkGroup(String groupName){
        if(groupMap.get(groupName)==null){
            return false;
        }
        return true;
    }
}
