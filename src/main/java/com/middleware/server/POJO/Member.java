package com.middleware.server.POJO;

import com.middleware.server.naming.ServiceInfo;

import java.io.Serializable;
import java.util.Set;

public class Member extends IpAndPort implements Serializable {
    String ip;
    String port;
    boolean alive;
    transient Set<String> providers;
    public Member(String ip, String port) {
        this.ip = ip;
        this.port = port;
        alive=true;
    }

    public String IpAndPort(){
        return ip+":"+port;
    }
    public Member() {
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean getAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive() {
        return alive;
    }

    public Set<String> getProviders() {
        return providers;
    }

    public void setProviders(Set<String> providers) {
        this.providers = providers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj instanceof Member) {
            Member vo = (Member) obj;

            // 比较每个属性的值一致时才返回true
            if (vo.ip.equals(this.ip) && vo.port.equals(this.port))
                return true;
        }
        return false;
    }
    @Override
    public int hashCode() {
        return ip.hashCode() *port.hashCode();
    }
}
