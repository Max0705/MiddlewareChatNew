package com.rc.JMSchat;

import java.net.InetAddress;

public class Getip {

    public String main(String[] args) throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        String ip=addr.getHostAddress().toString(); //获取本机ip
        String hostName=addr.getHostName().toString(); //获取本机计算机名称
        return ip;
    }
}
