package com.middleware.server.naming;

import com.middleware.server.POJO.Member;

import java.io.Serializable;
import java.util.*;

public class ServiceInfo {
    private Set<Member> cluster;

    public ServiceInfo() {
        cluster= Collections.synchronizedSet(new HashSet<>());
    }

    public Set<Member> getCluster(){
        return cluster;
    }
    public boolean checkMember(Member member){
        if(cluster.contains(member)){
            return true;
        }
//        System.out.println(cluster.size());
        return false;
    }

    public  void newMember(Member member){
        cluster.add(member);
        return ;
    }

    public List<Member> flush(){
        //需要解除订阅的列表
        List<Member> list=new ArrayList<>();
        for(Member member:cluster){
//            System.out.println("flush:"+member.getAlive());
            if(member.getAlive()){
                member.setAlive(false);
            }
            else{
                //解除订阅
                list.add(member);
                cluster.remove(member);
            }
        }
        return list;
    }

    public void reAlive(Member member){
        cluster.remove(member);
        cluster.add(member);
//        System.out.println("beat"+member.getAlive());
//        System.out.println(member.getAlive());
//        System.out.println(cluster.size());
    }


    public void setCluster(Set<Member> cluster) {
        this.cluster = cluster;
    }
    public void deleteMember(Member member){
        cluster.remove(member);
    }
}
