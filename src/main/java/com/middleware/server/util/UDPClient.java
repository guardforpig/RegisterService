package com.middleware.server.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    public static void send(String sendMsg,String ip,Integer port) throws IOException {
        InetAddress addr = InetAddress.getByName(ip);

        // 创建packet包对象，封装要发送的包数据和服务器地址和端口号
        DatagramPacket packet = new DatagramPacket(sendMsg.getBytes(),
                sendMsg.getBytes().length, addr, port);

        // 创建Socket对象
        DatagramSocket socket = new DatagramSocket();

        // 发送消息到服务器
        socket.send(packet);

        // 关闭socket
        socket.close();
    }
}
