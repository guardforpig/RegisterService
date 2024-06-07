package com.middleware.server.naming;

import com.alibaba.fastjson.JSON;
import com.middleware.server.POJO.Member;
import com.middleware.server.util.BroadcastUdp;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GroupInfo {
    private ConcurrentHashMap<String, ServiceInfo> serviceMap;
    private ConcurrentHashMap<String, Set<Member>> subscribeMap;
    public GroupInfo() {
        serviceMap=new ConcurrentHashMap<>();
        subscribeMap=new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, ServiceInfo> getServiceMap(){
        return serviceMap;
    }
    public ServiceInfo getService(String serviceName){
        if(checkService(serviceName)){
            return serviceMap.get(serviceName);
        }else{
            return null;
        }
    }

    public boolean checkService(String serviceName){
        if(serviceMap.get(serviceName)==null){
            return false;
        }
        return true;
    }

    public ServiceInfo newService(String serviceName){
        ServiceInfo service=new ServiceInfo();
        serviceMap.put(serviceName,service);
        if(!subscribeMap.containsKey(serviceName)) subscribeMap.put(serviceName,new HashSet<Member>());
        return service;
    }

    public Map<String,ServiceInfo> subscribe(Set<String> producerSet, Member member){
        for(String producerName:producerSet){
            Set list=subscribeMap.get(producerName);
            if(list==null){
                Set<Member> mSet=new HashSet<>();
                mSet.add(member);
                subscribeMap.put(producerName,mSet);
            }
            else{
                list.add(member);
            }
        }
        return obtainSubscribe(producerSet);
    }

    public Map<String,ServiceInfo> obtainSubscribe(Set<String> producerSet){
        Map<String,ServiceInfo> producersMap=new HashMap<>();
        for(String producerName:producerSet){
            producersMap.put(producerName,serviceMap.get(producerName));
        }
        return producersMap;
    }

    public void flush() throws Exception {
        if(!serviceMap.isEmpty()){
            for(String key:serviceMap.keySet()){
                ServiceInfo serviceInfo=serviceMap.get(key);
                List<Member> list=serviceInfo.flush();
                if(list!=null&&!list.isEmpty()){
                    //UDP广播
                    Map<String,Object> map=new HashMap<>();
                    map.put(key,serviceInfo);
                    String msg= JSON.toJSONString(map);
                    Set<Member> consumers=subscribeMap.get(key);
                    if(consumers!=null) new BroadcastUdp(msg,new ArrayList<>(consumers)).start();
                    if(serviceInfo.getCluster().size()==0){
                        serviceMap.remove(key);
                    }
                    for(Member member:list){
                        relieveSub(member);
                    }
                }
                if(serviceInfo.getCluster().isEmpty()){
                    serviceMap.remove(key);
                }
            }
        }
    }

    public List<Member> getConsumers(String serviceName){;
        if(subscribeMap.get(serviceName)!=null) return new ArrayList<>(subscribeMap.get(serviceName));
        else return null;
    }

    //删去这个实例的所有订阅
    public void relieveSub(Member member){
        if(member.getProviders()!=null)
            for(String name:member.getProviders()){
                Set<Member> set=subscribeMap.get(name);
                set.remove(member);
                if(set.size()==0){
                    subscribeMap.remove(name);
                }
            }
    }
}
