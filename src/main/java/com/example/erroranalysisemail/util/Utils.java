package com.example.erroranalysisemail.util;

import org.springframework.stereotype.Component;

@Component
public class Utils {
    
    public String getServerName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown-server";
        }
    }
}
