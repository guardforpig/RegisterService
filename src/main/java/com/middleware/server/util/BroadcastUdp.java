package com.middleware.server.util;

import com.middleware.server.POJO.IpAndPort;
import com.middleware.server.POJO.Member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.middleware.server.util.UDPClient.send;

public class BroadcastUdp extends Thread {
    String msg;
    List<IpAndPort> objects;

    @Override
    public void run() {
        if(objects==null||objects.isEmpty()){
            return;
        }
        for (IpAndPort member : objects) {
            try {
                send(msg, member.getIp(), Integer.valueOf(member.getPort()));
                System.out.println(member.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BroadcastUdp(String msg, Collection<Member> list) throws Exception {
        List<IpAndPort> objects=new ArrayList<>();
        objects.addAll(list);
        this.msg = msg;
        this.objects = objects;
    }
}
